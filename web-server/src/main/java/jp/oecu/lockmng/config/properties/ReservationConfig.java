package jp.oecu.lockmng.config.properties;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.resv")
@Data
@Validated
public class ReservationConfig {
    @Positive
    private int max_per_user = 1;
    @Positive
    private int min_duration_minute = 5;
    @Positive
    private int max_duration_minute = 1440;
    @NotNull
    private LocalTime available_start = LocalTime.MIDNIGHT;
    @NotNull
    private LocalTime available_end = LocalTime.MIDNIGHT;
}