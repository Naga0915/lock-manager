package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.password")
@Data
public class PasswordConfig {
    private int length = 8;
    private String algorithm = "bf";
}
