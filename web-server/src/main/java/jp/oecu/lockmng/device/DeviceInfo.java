package jp.oecu.lockmng.device;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.oecu.lockmng.config.properties.LockConfig;
import jp.oecu.lockmng.device.command.CommandParser;
import jp.oecu.lockmng.device.command.DeviceCommand;
import jp.oecu.lockmng.device.command.Operation;
import jp.oecu.lockmng.service.LockStateService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class DeviceInfo {
    private final Integer deviceId;
    private List<Integer> keyIds;
    private final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();
    private volatile boolean connected = false;
    private DeviceSession session;
    private final LockStateService lockStateService;
    private final LockConfig lockConfig;

    public DeviceInfo(Integer deviceId, List<Integer> keyIds, LockStateService lockStateService, LockConfig lockConfig) {
        this.deviceId = deviceId;
        this.keyIds = keyIds;
        this.lockStateService = lockStateService;
        this.lockConfig = lockConfig;
    }

    public void lock(Integer lockId) {
        DeviceCommand command = DeviceCommand.lock(lockId);
        sendCommand(command);
        log.info(String.format("ロック命令を送信しました: device=%d key=%d", deviceId, lockId));
    }

    public void unlock(Integer lockId) {
        DeviceCommand command = DeviceCommand.unlock(lockId);
        sendCommand(command);
        log.info(String.format("アンロック命令を送信しました: device=%d key=%d", deviceId, lockId));
    }
        
    public void sendCommand(DeviceCommand command){
        if(connected){
            String message = CommandParser.toRaw(command);
            sendQueue.offer(message);
        }
    }

    public void handleIncomingCommand(DeviceCommand command){
        log.info(String.format("[ESP-%d]: %s", deviceId, command.getRaw()));
        try{
            if(command.getOperation() == Operation.IM_ALIVE){
                int deviceId1 = Integer.parseInt(command.getOperand());
                if(deviceId1 == deviceId){
                    if(session != null){
                        session.imAlive();
                    }
                }
            }else if(command.getOperation() == Operation.LOCK_SUCCESS){
                int lockId = Integer.parseInt(command.getOperand());
                lockStateService.createOrUpdate(lockId, "l");
            }else if(command.getOperation() == Operation.UNLOCK_SUCCESS){
                int lockId = Integer.parseInt(command.getOperand());
                lockStateService.createOrUpdate(lockId, "u");
            }
        }catch(Exception e){
            log.error("デバイスからのメッセージを処理できませんでした。", e);
        }
    }
}