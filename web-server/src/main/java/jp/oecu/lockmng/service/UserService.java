package jp.oecu.lockmng.service;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.oecu.lockmng.config.properties.PasswordConfig;
import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.repository.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordConfig passwordConfig;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, PasswordConfig passwordConfig){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordConfig = passwordConfig;
    }

    public Optional<String> newUser(NewUserModel model){
        if(!model.getPassword().equals(model.getPasswordCheck())){
            return Optional.of("パスワードが一致しません。");
        }
        if(model.getPassword().length() < passwordConfig.getLength()){
            return Optional.of(String.format("パスワードの長さが足りません。 (%d文字以上)", passwordConfig.getLength()));
        }
        if(repository.existsByIdStr(model.getUserId())){
            return Optional.of("そのユーザIDはすでに使われています。");
        }
        User user = new User();
        user.setIdStr(model.getUserId());
        user.setName(model.getUserName());
        user.setEmail(model.getUserEmail());
        user.setPhone(model.getUserPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(model.getPassword()));
        try { 
            repository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }
}
