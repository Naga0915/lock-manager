package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.com")
@Data
public class AppCommonConfig {
    private String base_url = "http://localhost:8080";
    private String timezone = "Asia/Tokyo";
}
