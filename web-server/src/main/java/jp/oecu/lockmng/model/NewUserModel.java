package jp.oecu.lockmng.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Valid
@Data
public class NewUserModel {
    @NotNull
    private String registrationId;
    @NotNull
    private String userId;
    @NotNull
    private String userName;
    @NotNull
    private String userEmail;
    @NotNull
    private String userPhoneNumber;
    @NotNull
    private String password;
    @NotNull
    private String passwordCheck;
}
