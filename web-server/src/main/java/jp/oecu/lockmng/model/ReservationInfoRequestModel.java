package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Valid
public class ReservationInfoRequestModel {
    @NotNull
    @Positive
    private Integer year;
    @NotNull
    @Positive
    private Integer month;
    @NotNull
    @Positive
    private Integer day;
}
