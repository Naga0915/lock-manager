package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class UserContentController {
    @GetMapping("/user")
    public String home(){
        return "user/home.html";
    }

    @GetMapping("/user/lock")
    public String lock() {
        return "user/button.html";
    }
    
}