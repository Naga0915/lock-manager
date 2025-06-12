package jp.oecu.lockmng.config;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jp.oecu.lockmng.config.properties.AppCommonConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Data
public class TimeZoneConfig {
    private final AppCommonConfig appCommonConfig;
    private final ZoneId zoneId;

    public TimeZoneConfig(AppCommonConfig appCommonConfig){
        this.appCommonConfig = appCommonConfig;
        ZoneId temp;
        try{
            temp = ZoneId.of(appCommonConfig.getTimezone());
        }catch (Exception e){
            log.error("タイムゾーンが無効です。システムのデフォルトを使用します。", e);
            temp = ZoneId.systemDefault();
        }
        this.zoneId = temp;
    }

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone(zoneId.getId()));
    }
}
