package jp.oecu.lockmng.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import jp.oecu.lockmng.device.command.CommandParser;
import jp.oecu.lockmng.device.command.DeviceCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceSession {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final BlockingQueue<String> sendQueue;
    private final DeviceInfo device;
    private AtomicInteger isAliveCount = new AtomicInteger(0);

    public DeviceSession(Socket socket, BufferedReader reader, BufferedWriter writer, BlockingQueue<String> sendQueue, DeviceInfo device) throws IOException{
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.sendQueue = sendQueue;
        this.device = device;
    }

    public void imAlive(){
        this.isAliveCount.set(0);
    }

    public void startCommunicationThreads() {
        new Thread(this::receiveLoop, "ESP-Recv").start();
        new Thread(this::sendLoop, "ESP-Send").start();
        new Thread(this::startAliveThread, "ESP-IsAlive").start();
    }

    private void startAliveThread() {
        try {
            while (!socket.isClosed()) {
                DeviceCommand command = DeviceCommand.isAlive(device.getDeviceId());
                device.sendCommand(command);
                isAliveCount.incrementAndGet();
                Thread.sleep(device.getLockConfig().getTimeout_millisecond()); // 10秒ごとに送信
                if(isAliveCount.get() > device.getLockConfig().getTimeout_count_before_disconnect()){
                    log.warn("タイムアウトの制限に達したので、切断します");
                    close();
                }
            }
        } catch (InterruptedException e) {
            log.warn("IS_ALIVEスレッド中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void sendLoop(){
        try {
            while (true) {
                String msg = sendQueue.take(); // ブロッキング待ち
                writer.write(msg + "\n");
                writer.flush();
                log.info("[To ESP]: " + msg);
            }
        } catch (IOException | InterruptedException e) {
            log.info("送信スレッド終了: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void receiveLoop(){
        try {
            String line;
            while ((line = reader.readLine()) != null) {
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
        try {
            socket.close();
            reader.close();
            writer.close();
            device.setConnected(false);
            isAliveCount.set(0);
        } catch (IOException e) {
            log.error("Socket close failed: " + e.getMessage());
        }
    }
}
