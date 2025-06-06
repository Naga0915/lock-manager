package jp.oecu.lockmng.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

//これらの設定はinit.sqlと同期する必要がある
@Component
@ConfigurationProperties(prefix = "app.user.info")
@Data
public class UserInfoConfig {
    private int id_min = 1;
    private int id_max = 30;
    private int name_min = 1;
    private int name_max = 100;
}