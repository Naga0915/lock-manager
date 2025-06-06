package jp.oecu.lockmng.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.session")
@Data
@EnableJdbcHttpSession
public class SpringSessionConfig {
    private int max_sessions = 1;
}
