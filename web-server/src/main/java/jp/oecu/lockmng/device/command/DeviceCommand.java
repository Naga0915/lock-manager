package jp.oecu.lockmng.device.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceCommand {
    private Operation operation;
    private String operand;
    private String raw;

    public List<Integer> getKeyIds() {
        if (operation != Operation.I_HAVE) {
            throw new IllegalStateException("getKeyIds is only valid for I_HAVE operation");
        }

        if (operand == null || operand.isBlank()) {
            return List.of();
        }

        return Arrays.stream(operand.split(","))
            .map(String::trim)
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }

    public static DeviceCommand isAlive(Integer deviceId) {
        String operand = deviceId.toString();
        Operation operation = Operation.IS_ALIVE;
        String raw = operation.toCommandString(operand);
        return new DeviceCommand(operation, operand, raw);
    }

    public static DeviceCommand lock(Integer lockId) {
        String operand = lockId.toString();
        Operation operation = Operation.LOCK;
        String raw = operation.toCommandString(operand);
        return new DeviceCommand(operation, operand, raw);
    }

    public static DeviceCommand unlock(Integer lockId) {
        String operand = lockId.toString();
        Operation operation = Operation.UNLOCK;
        String raw = operation.toCommandString(operand);
        return new DeviceCommand(operation, operand, raw);
    }
}
