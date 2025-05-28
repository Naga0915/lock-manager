package jp.oecu.lockmng.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.oecu.lockmng.config.properties.PasswordConfig;
import jp.oecu.lockmng.config.properties.UserInfoConfig;
import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.repository.jpa.UserRepository;
import jp.oecu.lockmng.util.CommonValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordConfig passwordConfig;
    private final UserInfoConfig userInfoConfig;
    private final CommonValidator commonValidator;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, PasswordConfig passwordConfig, CommonValidator commonValidator, UserInfoConfig userInfoConfig){
        this.userInfoConfig = userInfoConfig;
        this.commonValidator = commonValidator;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordConfig = passwordConfig;
    }

    public Optional<String> newUser(NewUserModel model){
        if(model.getPassword() != null)
        if(!model.getPassword().equals(model.getPasswordCheck())){
            return Optional.of("パスワードが一致しません。");
        }
        if(model.getPassword().length() > passwordConfig.getLength_max() || model.getPassword().length() < passwordConfig.getLength_min()){
            return Optional.of(String.format("パスワードは%d文字以上%d文字以下", passwordConfig.getLength_min(), passwordConfig.getLength_max()));
        }
        if(model.getUserId().length() > userInfoConfig.getId_max() || model.getUserId().length() < userInfoConfig.getId_min()){
            return Optional.of(String.format("ユーザIDは%d文字以上%d文字以内", userInfoConfig.getId_min(), userInfoConfig.getId_max()));
        }
        if(model.getUserName().length() > userInfoConfig.getName_max() || model.getUserName().length() < userInfoConfig.getName_min()){
            return Optional.of(String.format("氏名は%d文字以上%d文字以内", userInfoConfig.getName_min(), userInfoConfig.getName_max()));
        }
        if(model.getUserEmail().isBlank() && model.getUserPhoneNumber().isBlank()){
            return Optional.of("Eメールアドレスか電話番号が必要");
        }
        if(!commonValidator.isEmailValid(model.getUserEmail())){
            return Optional.of(String.format("メールアドレスの形式が不正： %s", model.getUserEmail()));
        }
        if(repository.existsByIdStr(model.getUserId())){
            return Optional.of("そのユーザIDはすでに使われています。");
        }
        User user = new User();
        user.setIdStr(model.getUserId());
        user.setName(model.getUserName());
        user.setEmail(model.getUserEmail());
        user.setPhone(model.getUserPhoneNumber());
        user.setRole("USER");
        user.setPasswordHash(passwordEncoder.encode(model.getPassword()));
        try { 
            repository.save(user);
        } catch (Exception e) {
            log.error("ユーザ登録失敗：", e);
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }
}
