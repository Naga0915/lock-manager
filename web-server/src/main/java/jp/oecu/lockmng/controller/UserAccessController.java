package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserAccessController {
    @GetMapping("/login")
    public String login() {
        return "login.html";
    }
    
    @GetMapping("/logout")
    public String logout(){
        return "logout.html";
    }
}
