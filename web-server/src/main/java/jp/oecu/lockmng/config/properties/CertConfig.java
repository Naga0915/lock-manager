package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.cert")
@Data
@Validated
public class CertConfig {
    @NotNull
    @Min(1)
    @Max(65535)
    private Integer tls_port = 8000;
    @NotNull
    private String folder_name = "cert";
    @Positive
    private Integer rsa_keySize = 2048;
    @NotNull
    private String cn = "ESP32";
    @NotNull
    private String o = "OECU";
    @NotNull
    private String ou = "ESPLock";
    @NotNull
    private String l = "Osaka";
    @NotNull
    private String c = "JP";
}