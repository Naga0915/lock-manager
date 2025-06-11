package jp.oecu.lockmng.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Valid
@Data
public class NewReserveModel implements Serializable{
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotNull
    @Positive
    private int lockerId;
}
