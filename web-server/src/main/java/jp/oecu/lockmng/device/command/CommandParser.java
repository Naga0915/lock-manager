package jp.oecu.lockmng.device.command;

public class CommandParser {
    public static DeviceCommand parse(String rawMessage) {
        String[] parts = rawMessage.trim().split("\\s+", 2);
        if (parts.length == 0) throw new IllegalArgumentException("Empty message");

        String opPart = parts[0];
        String operandPart = parts.length > 1 ? parts[1] : "";

        // SUCCESS / FAILED メッセージ対応
        if (operandPart.endsWith("SUCCESS")) {
            return new DeviceCommand(Operation.fromString(opPart + "_SUCCESS"),
                                     operandPart.replace(" SUCCESS", ""), rawMessage);
        } else if (operandPart.endsWith("FAILED")) {
            return new DeviceCommand(Operation.fromString(opPart + "_FAILED"),
                                     operandPart.replace(" FAILED", ""), rawMessage);
        }

        return new DeviceCommand(Operation.fromString(opPart), operandPart, rawMessage);
    }
    
    public static String toRaw(DeviceCommand command) {
        return command.getOperation().toCommandString(command.getOperand());
    }
}