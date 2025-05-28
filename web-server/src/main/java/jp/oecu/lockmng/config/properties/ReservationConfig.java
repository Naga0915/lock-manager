package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.resv")
@Data
public class ReservationConfig {
    private int max = 1;
}