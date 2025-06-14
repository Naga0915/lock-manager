package jp.oecu.lockmng.device;

import lombok.Data;

@Data
public class LockInfo {
    private final int internalId;
    private final int serverSideId;
    private boolean isControllable;
    private LockState state;

    public LockInfo(int internalId, int serverSideId, boolean isControllable, LockState state) {
        this.internalId = internalId;
        this.isControllable = isControllable;
        this.state = state;
        this.serverSideId = serverSideId;
    }
}
