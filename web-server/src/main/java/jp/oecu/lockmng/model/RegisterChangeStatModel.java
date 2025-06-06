package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Valid
@Data
public class RegisterChangeStatModel {
    @NotNull
    private String uuid;
    @NotNull
    private boolean enabled;
}
