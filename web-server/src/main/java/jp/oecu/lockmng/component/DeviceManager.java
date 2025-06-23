package jp.oecu.lockmng.component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import jp.oecu.lockmng.component.tls.TLSUtil;
import jp.oecu.lockmng.config.properties.CertConfig;
import jp.oecu.lockmng.config.properties.LockConfig;
import jp.oecu.lockmng.device.DeviceInfo;
import jp.oecu.lockmng.device.DeviceSession;
import jp.oecu.lockmng.device.command.CommandParser;
import jp.oecu.lockmng.device.command.DeviceCommand;
import jp.oecu.lockmng.device.command.Operation;
/*
     * espとのメッセージ定義
     * サーバからESP：
     * "UNLOCK Y" 鍵ID Y を開ける
     * "LOCK Y" 鍵ID Y を閉める
     * "IS_ALIVE X" 端末ID X の生存確認
     * 
     * ESPからサーバ
     * "MY_ID_IS X" 端末のIDを伝える ESP側でIDは設定してあるので、それをサーバーに伝える。
     * "I_HAVE Y1,Y2,Y3,Y4" 端末の鍵のIDを知らせる 鍵IDは管理者が自分で管理
     * 
     * "UNLOCK Y SUCCESS" 鍵ID Y を開けました
     * "LOCK Y SUCCESS" 鍵ID Y を閉めました
     * "UNLOCK Y FAILED" 鍵ID Y を開けれませんでした
     * "LOCK Y FAILED" 鍵ID Y を閉めれませんでした
     * "IM_ALIVE X" 端末ID X の生存確認応答
     * 
     * ESPは接続初期化時にすべての鍵を閉めているとする。
     * ESPは接続が途中で切れる可能性があるので、切断時は、そのデバイスにある鍵を無効にする必要がある。
     * また、再接続時には鍵を再度有効にする必要がある
     */
     import jp.oecu.lockmng.service.LockStateService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeviceManager {

    private final CertConfig certConfig;
    private final LockConfig lockConfig;
    private final CertManager certManager;
    private final LockStateService lockStateService;
    private final Map<Integer, DeviceSession> sessions = new ConcurrentHashMap<>();
    private final Map<Integer, DeviceInfo> devices = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> locks = new ConcurrentHashMap<>();
    private Thread currentListener;

    @Autowired
    public DeviceManager(LockConfig lockConfig, LockStateService lockStateService, CertManager certManager, CertConfig certConfig) {
        this.lockConfig = lockConfig;
        this.certManager = certManager;
        this.lockStateService = lockStateService;
        this.certConfig = certConfig;
        currentListener = null;
    }

    @PostConstruct
    public void startListener() {
        String uuid = UUID.randomUUID().toString().substring(0, 5);
        Thread listenerThread = new Thread(this::listenerLoop, "lsnLoop-" + uuid);
        listenerThread.setDaemon(true); // アプリ終了時に終了させる場合
        listenerThread.start();
    }

    @PreDestroy
    private void destroyListener(){
        if (currentListener != null && currentListener.isAlive()) {
            currentListener.interrupt();
        }
    }

    private void listenerLoop() {
        while (true) {
            String uuid = UUID.randomUUID().toString().substring(0, 5);
            Thread listenerThread = new Thread(this::handleConnection, "listener-" + uuid);
            listenerThread.setDaemon(true);
            listenerThread.start();
            currentListener = listenerThread;

            try {
                // スレッドが終了するのを待つ（join）
                listenerThread.join();
                log.warn("handleConnection スレッドが終了しました。5秒後に再起動します...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("listenerLoop が割り込まれました。終了します。");
                break;
            }
        }
    }

    public Optional<String> tryLock(Integer lockId){
        if(locks.containsKey(lockId)){
            DeviceInfo device = devices.get(locks.get(lockId));
            if(!device.isConnected()) return Optional.of(String.format("鍵ID: %d 現在使用できません", lockId));
            device.lock(lockId);
            return Optional.empty();
        }
        return Optional.of(String.format("鍵ID: %d は存在しません", lockId));
    }

    public Optional<String> tryUnlock(Integer lockId){
        if(locks.containsKey(lockId)){
            DeviceInfo device = devices.get(locks.get(lockId));
            if(!device.isConnected()) return Optional.of(String.format("鍵ID: %d 現在使用できません", lockId));
            device.unlock(lockId);
            return Optional.empty();
        }
        return Optional.of(String.format("鍵ID: %d は存在しません", lockId));
    }

    public void handleConnection() {
        try (ServerSocket sslServerSocket = TLSUtil.createTLSServerSocket(certConfig.getTls_port(), certManager)) {
            log.info("ESP接続待機開始...");

            while (true) {
                Socket socket = sslServerSocket.accept();
                log.info("新しいESP接続: " + socket.getRemoteSocketAddress());

                // 初期化を別スレッドで非同期実行
                String uuid = UUID.randomUUID().toString().substring(0, 5);
                Thread thread = new Thread(() -> initializeDevice(socket), "DevInit-" + uuid);
                thread.start();            
            }
        } catch (Exception e) {
            log.error("handleConnection エラー: ", e);
        }
    }

    private void initializeDevice(Socket socket) {
        try {
            socket.setSoTimeout(lockConfig.getTimeout_millisecond() * lockConfig.getTimeout_count_before_disconnect());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 1. MY_ID_IS X を受信
            String myIdLine = reader.readLine();
            if (myIdLine == null) throw new IOException("MY_ID_IS を受信できませんでした");
            DeviceCommand myIdCommand = CommandParser.parse(myIdLine);
            if (myIdCommand.getOperation() != Operation.MY_ID_IS) {
                log.warn("不正な初期メッセージ: " + myIdLine);
                socket.close();
                return;
            }
            Integer deviceId = Integer.parseInt(myIdCommand.getOperand());

            // 2. I_HAVE Y1,Y2,... を受信
            String keyIdsLine = reader.readLine();
            if (keyIdsLine == null) throw new IOException("I_HAVE を受信できませんでした");
            DeviceCommand iHaveCommand = CommandParser.parse(keyIdsLine);
            if (iHaveCommand.getOperation() != Operation.I_HAVE) {
                log.warn("不正な鍵情報メッセージ: " + keyIdsLine);
                socket.close();
                return;
            }
            List<Integer> keyIds = iHaveCommand.getKeyIds();

            // 3. DeviceInfo を用意
            DeviceInfo deviceInfo = devices.compute(deviceId, (id, existing) -> {
                if (existing != null) {
                    log.info("既存デバイスID: {} に再接続", id);
                    existing.setConnected(true);
                    existing.setKeyIds(keyIds);
                    return existing;
                } else {
                    log.info("新しいデバイスID: {} を登録", id);
                    return new DeviceInfo(deviceId, keyIds, lockStateService, lockConfig);
                }
            });

            for (Integer keyId : keyIds) {
                Integer previousOwner = locks.put(keyId, deviceId);
                if (previousOwner != null && !previousOwner.equals(deviceId)) {
                    log.warn("鍵ID {} がデバイス {} から {} に移動しました", keyId, previousOwner, deviceId);
                } else {
                    log.info("鍵ID {} をデバイス {} に登録", keyId, deviceId);
                }
            }

            DeviceSession oldSession = sessions.get(deviceId);
            if (oldSession != null) {
                log.info("既存セッションを閉じます: deviceId={}", deviceId);
                oldSession.close();
                sessions.remove(deviceId);
            }

            // 4. DeviceSession 作成 & 起動
            DeviceSession session = new DeviceSession(socket, reader, writer, deviceInfo.getSendQueue(), deviceInfo, this);
            deviceInfo.setSession(session);
            deviceInfo.setConnected(true);
            session.startCommunicationThreads();

            // 5. sessions に登録
            sessions.put(deviceId, session);
            log.info("デバイス {} 接続完了", deviceId);

        } catch (Exception e) {
            log.error("接続初期化中にエラーが発生しました: ", e);
            try {
                socket.close();
            } catch (Exception closeEx) {
                log.warn("ソケットクローズに失敗: ", closeEx);
            }
        }
    }

    public void closeSession(Integer deviceId){
        DeviceSession session = sessions.get(deviceId);
        if(session != null){
            sessions.remove(deviceId);
        }
        DeviceInfo deviceInfo = devices.get(deviceId);
        if(deviceInfo != null){
            deviceInfo.setSession(null);
        }
    }
}
