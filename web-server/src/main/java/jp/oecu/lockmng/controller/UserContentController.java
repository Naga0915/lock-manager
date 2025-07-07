package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class UserContentController {
    @GetMapping("/user")
    public String home(){
        return "user/home.html";
    }
    
    @GetMapping("/user/register_guide")
    public String registerguide(){
        return "user/register_guide.html";
    }

    @GetMapping("/user/faq")
    public String faq(){
        return "user/faq.html";
    }
}