package jp.oecu.lockmng.device.command;

public enum Operation {
    // サーバー → ESP
    UNLOCK,      // UNLOCK Y
    LOCK,        // LOCK Y
    IS_ALIVE,    // IS_ALIVE X

    // ESP → サーバー
    MY_ID_IS,    // MY_ID_IS X
    I_HAVE,      // I_HAVE Y1,Y2,Y3,Y4
    UNLOCK_SUCCESS, // UNLOCK Y SUCCESS
    LOCK_SUCCESS,   // LOCK Y SUCCESS
    UNLOCK_FAILED,  // UNLOCK Y FAILED
    LOCK_FAILED,    // LOCK Y FAILED
    IM_ALIVE;       // IM_ALIVE X

    public static Operation fromString(String text) {
        return switch (text) {
            case "UNLOCK" -> UNLOCK;
            case "LOCK" -> LOCK;
            case "IS_ALIVE" -> IS_ALIVE;
            case "MY_ID_IS" -> MY_ID_IS;
            case "I_HAVE" -> I_HAVE;
            case "UNLOCK_SUCCESS" -> UNLOCK_SUCCESS;
            case "LOCK_SUCCESS" -> LOCK_SUCCESS;
            case "UNLOCK_FAILED" -> UNLOCK_FAILED;
            case "LOCK_FAILED" -> LOCK_FAILED;
            case "IM_ALIVE" -> IM_ALIVE;
            default -> throw new IllegalArgumentException("Unknown operation: " + text);
        };
    }

    public String toCommandString(String operand) {
        return switch (this) {
            case UNLOCK, LOCK, IS_ALIVE, MY_ID_IS, I_HAVE -> this.name() + " " + operand;
            case UNLOCK_SUCCESS, LOCK_SUCCESS, UNLOCK_FAILED, LOCK_FAILED, IM_ALIVE -> {
                String[] parts = this.name().split("_");
                yield parts[0] + " " + operand + " " + parts[1];
            }
        };
    }
}