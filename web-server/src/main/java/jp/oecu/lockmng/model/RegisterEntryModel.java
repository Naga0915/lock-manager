package jp.oecu.lockmng.model;

import java.time.ZonedDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Valid
@Data
public class RegisterEntryModel {
    @NotNull
    private String uuid;
    private String memo;
    @NotNull
    private boolean isEnable;
    @NotNull
    private ZonedDateTime created;
    @NotNull
    private ZonedDateTime expire;
    private String idStr;
    private String ipAddress;
    private String userAgent;
}
