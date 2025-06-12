package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.user.register")
@Data
@Validated
public class RegisterConfig {
    @Positive
    private int expire_hour = 24;
}
