package jp.oecu.lockmng.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jp.oecu.lockmng.service.UserDetailService;

@Configuration
public class SecurityConfig {
    private final UserDetailService userDetailService;
    private final SpringSessionConfig springSessionConfig;

    @Autowired
    public SecurityConfig(UserDetailService userDetailService, SpringSessionConfig springSessionConfig){
        this.userDetailService = userDetailService;
        this.springSessionConfig = springSessionConfig;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers("/login").permitAll()
                // .requestMatchers("/logout").authenticated()
                // //.requestMatchers("/admin/**").hasRole("ADMIN") //本番用
                // .requestMatchers("/admin/**").permitAll() //テスト用
                // .requestMatchers("/error").permitAll()
                // .requestMatchers("/register/**").permitAll()
                // .requestMatchers("/reset").permitAll()
                // .requestMatchers("/").permitAll()
                // .requestMatchers("/r/**").permitAll()
                .anyRequest().permitAll()
            ).formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?s=0")
                .usernameParameter("userid")
                .passwordParameter("password")
                .permitAll()
            ).logout(logout->logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?s=1")
                .invalidateHttpSession(true)
                .deleteCookies("SESSION")
            ).sessionManagement(session -> session
                .maximumSessions(springSessionConfig.getMax_sessions()) // 多重ログインを禁止
                .maxSessionsPreventsLogin(true)
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception{
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
            .userDetailsService(userDetailService)
            .passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
