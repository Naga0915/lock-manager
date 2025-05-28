package jp.oecu.lockmng.service;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.repository.jpa.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> newUser(NewUserModel model){
        if(!model.getPassword().equals(model.getPasswordCheck())){
            return Optional.of("パスワードが一致しません");
        }
        User user = new User();
        user.setIdStr(model.getUserId());
        user.setName(model.getUserName());
        user.setEmail(model.getUserEmail());
        user.setPhone(model.getUserPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(model.getPassword()));
        repository.save(user);
        return Optional.empty();
    }
}
