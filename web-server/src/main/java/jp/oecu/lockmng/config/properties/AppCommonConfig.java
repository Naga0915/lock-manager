package jp.oecu.lockmng.config.properties;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.com")
@Data
@Validated
public class AppCommonConfig {

    @NotBlank
    @URL(message = "有効なURLを指定してください")
    private String base_url = "http://localhost:8080";

    @NotBlank
    @Pattern(
        regexp = "^[A-Za-z_]+/[A-Za-z_]+$",
        message = "timezone は 'Asia/Tokyo' のような形式で指定してください"
    )
    private String timezone = "Asia/Tokyo";
}