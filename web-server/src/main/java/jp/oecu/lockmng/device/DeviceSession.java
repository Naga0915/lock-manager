package jp.oecu.lockmng.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jp.oecu.lockmng.component.DeviceManager;
import jp.oecu.lockmng.device.command.CommandParser;
import jp.oecu.lockmng.device.command.DeviceCommand;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class DeviceSession {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final BlockingQueue<String> sendQueue;
    private final DeviceInfo device;
    private final DeviceManager deviceManager;
    private AtomicInteger isAliveCount = new AtomicInteger(0);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile boolean running = true;

    public DeviceSession(Socket socket, BufferedReader reader, BufferedWriter writer, BlockingQueue<String> sendQueue, DeviceInfo device, DeviceManager deviceManager) throws IOException{
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.sendQueue = sendQueue;
        this.device = device;
        this.deviceManager = deviceManager;
    }

    public void imAlive(){
        this.isAliveCount.set(0);
    }

    public void startCommunicationThreads() {
        String uuid = UUID.randomUUID().toString().substring(0, 5);
        new Thread(this::receiveLoop, "ESP-Recv-" + uuid).start();
        new Thread(this::sendLoop, "ESP-Send-" + uuid).start();
        new Thread(this::startAliveThread, "ESP-IsAlive-" + uuid).start();
        for (Integer lockId : device.getKeyIds()) {
            device.sendCommand(DeviceCommand.lock(lockId));
        }
    }

    private void startAliveThread() {
        try {
            while (running && !socket.isClosed()) {
                DeviceCommand command = DeviceCommand.isAlive(device.getDeviceId());
                device.sendCommand(command);
                isAliveCount.incrementAndGet();
                Thread.sleep(device.getLockConfig().getTimeout_millisecond() / 2); // 10秒ごとに送信
                if(isAliveCount.get() > device.getLockConfig().getTimeout_count_before_disconnect()){
                    log.warn("タイムアウトの制限に達したので、切断します");
                    close();
                }
            }
        } catch (InterruptedException e) {
            log.warn("IS_ALIVEスレッド中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            close();
        }
    }
    private void sendLoop(){
        try {
            while (running) {
                String msg = sendQueue.take();
                writer.write(msg + "\n");
                writer.flush();
                log.info("[To ESP]: " + msg);
            }
        } catch (InterruptedException e) {
            log.info("送信スレッド中断");
            running = false;
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.info("送信スレッド終了: " + e.getMessage());
        } finally {
            close();
        }
    }
    private void receiveLoop(){
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                DeviceCommand command = CommandParser.parse(line);
                device.handleIncomingCommand(command);
            }
        } catch (IOException e) {
            log.info("受信スレッド終了: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return; // すでに閉じられているので無視
        }

        running = false; // スレッドループを終了させる
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Socket close failed: " + e.getMessage());
        }
        try {
            reader.close();
        } catch (IOException e) {
            log.error("Reader close failed: " + e.getMessage());
        }
        try {
            writer.close();
        } catch (IOException e) {
            log.error("Writer close failed: " + e.getMessage());
        }
        for (Integer lockId : device.getKeyIds()) {
            device.getLockStateService().createOrUpdate(lockId, "d");
        }
        device.setConnected(false);
        isAliveCount.set(0);
        deviceManager.closeSession(device.getDeviceId());
        log.info("DeviceSession closed for device " + device.getDeviceId());
    }
}
