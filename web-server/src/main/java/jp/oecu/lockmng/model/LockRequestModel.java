package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Valid
@Data
public class LockRequestModel {
    public final static String OP_LOCK = "LOCK";
    public final static String OP_UNLOCK = "UNLOCK";
    @NotNull
    @Min(0)
    private Integer lockId;
    @NotNull
    private String operation;
}
