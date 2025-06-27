package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Valid
public class UserControllModel {
    @NotNull
    private Integer userId;
    @NotNull
    private String operation;
    @NotNull
    private String operand;
}
