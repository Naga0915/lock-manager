package jp.oecu.lockmng.util;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jp.oecu.lockmng.entity.User;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final User user;

    public CustomUserDetails(User user) {
        super(user.getIdStr(), user.getPasswordHash(),
            user.isEnabled(),
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}