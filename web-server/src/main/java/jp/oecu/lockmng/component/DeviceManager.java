package jp.oecu.lockmng.component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLServerSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    public DeviceManager(LockConfig lockConfig, LockStateService lockStateService, CertManager certManager, CertConfig certConfig) {
        this.lockConfig = lockConfig;
        this.certManager = certManager;
        this.lockStateService = lockStateService;
        this.certConfig = certConfig;
    }

    public void handleConnection(){
        try (ServerSocket sslServerSocket = TLSUtil.createTLSServerSocket(certConfig.getTls_port(), certManager)) {
            log.info("ESP接続待機開始...");
            while (true) {
                Socket socket = sslServerSocket.accept();
                log.info("新しいESP接続: " + socket.getRemoteSocketAddress());
                
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    // 1. MY_ID_IS X を受信
                    String myIdLine = reader.readLine();
                    DeviceCommand myIdCommand = CommandParser.parse(myIdLine);
                    if (myIdCommand.getOperation() != Operation.MY_ID_IS) {
                        log.warn("不正な初期メッセージ: " + myIdLine);
                        socket.close();
                        continue;
                    }
                    Integer deviceId = Integer.parseInt(myIdCommand.getOperand());

                    // 2. I_HAVE Y1,Y2,... を受信
                    String keyIdsLine = reader.readLine();
                    DeviceCommand iHaveCommand = CommandParser.parse(keyIdsLine);
                    if (iHaveCommand.getOperation() != Operation.I_HAVE) {
                        log.warn("不正な鍵情報メッセージ: " + keyIdsLine);
                        socket.close();
                        continue;
                    }
                    List<Integer> keyIds = iHaveCommand.getKeyIds();

                    // 3. DeviceInfo を用意（既存 or 新規）
                    DeviceInfo deviceInfo = devices.compute(deviceId, (id, existing) -> {
                        if (existing != null) {
                            log.info("既存デバイスID: {} に再接続", id);
                            existing.setConnected(true);
                            return existing;
                        } else {
                            log.info("新しいデバイスID: {} を登録", id);
                            return new DeviceInfo(deviceId, keyIds, lockStateService, lockConfig);
                        }
                    });

                    // 追加: locks への登録
                    for (Integer keyId : keyIds) {
                        Integer previousOwner = locks.put(keyId, deviceId);
                        if (previousOwner != null && !previousOwner.equals(deviceId)) {
                            log.warn("鍵ID {} がデバイス {} から {} に移動しました", keyId, previousOwner, deviceId);
                        } else {
                            log.info("鍵ID {} をデバイス {} に登録", keyId, deviceId);
                        }
                    }

                    // 4. DeviceSession 作成 & 起動
                    DeviceSession session = new DeviceSession(socket, reader, writer, deviceInfo.getSendQueue(), deviceInfo);
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

        } catch (Exception e) {
            log.error("handleConnection エラー: ", e);
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
