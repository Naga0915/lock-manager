package jp.oecu.lockmng.model;

import lombok.Data;

@Data
public class NewUserModel {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String password;
    private String passwordCheck;
}
