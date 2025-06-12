package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.password")
@Data
@Validated
public class PasswordConfig {
    @Positive
    private int length_min = 8;
    @Positive
    private int length_max = 100;
}
