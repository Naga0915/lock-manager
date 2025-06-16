package jp.oecu.lockmng.device;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LockDevice {
    private int deviceId;
    private List<LockInfo> lockList;
    private boolean isControllable;

    public LockDevice(int deviceId, int lockNum, int serverSideIdOffset, boolean isControllable) {
        this.deviceId = deviceId;
        this.isControllable = isControllable;
        this.lockList = new ArrayList<LockInfo>();
        for (int i = 0; i < lockNum; i++) {
            lockList.add(new LockInfo(i, i + serverSideIdOffset, true, LockState.LOCKED));
        }
    }
}
