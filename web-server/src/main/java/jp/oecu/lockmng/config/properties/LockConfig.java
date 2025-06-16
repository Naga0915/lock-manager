package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.lock")
@Data
@Validated
public class LockConfig {
    @Positive
    private Integer num = 4;
    @Positive
    private Integer timeout_millisecond = 10000;
    @Positive
    private Integer timeout_count_before_disconnect = 5;
}