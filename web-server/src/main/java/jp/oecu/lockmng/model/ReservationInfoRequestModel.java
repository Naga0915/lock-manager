package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Min(1)
    @Max(12)
    private Integer month;
    @NotNull
    @Positive
    private Integer day;
    @NotNull
    @Min(0)
    private Integer lockId;
}
