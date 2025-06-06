package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import lombok.Data;

@Valid
@Data
public class NewRegisterModel {
    private String memo;
}
