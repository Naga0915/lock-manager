package jp.oecu.lockmng.model;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Valid
@Data
public class NewReserveModel implements Serializable{
    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;
    @NotNull
    @Positive
    private int lockId;
}
