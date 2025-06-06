package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Valid
@Data
public class LockRequestModel {
    @NotNull
    private String lockId;
    @NotNull
    private String operation;
}
