package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;
import lombok.Data;

//これらの設定はinit.sqlと同期する必要がある
@Component
@ConfigurationProperties(prefix = "app.user.info")
@Data
@Validated
public class UserInfoConfig {
    @Positive
    private int id_min = 1;
    @Positive
    private int id_max = 30;
    @Positive
    private int name_min = 1;
    @Positive
    private int name_max = 100;
}