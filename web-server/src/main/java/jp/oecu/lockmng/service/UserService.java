package jp.oecu.lockmng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.repository.jpa.UserRepository;

@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByIdStr(username).orElseThrow(() -> new UsernameNotFoundException("user id=\"" + username + "\" not found"));
        return new org.springframework.security.core.userdetails.User(
            user.getIdStr(),
            user.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
