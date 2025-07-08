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

    @GetMapping("/user/method")
    public String method(){
        return "user/method.html";
    }
    
    @GetMapping("/admin/login")
    public String login(){
        return "user/trap_fbi.html";
    }
    @GetMapping("/administrator")
    public String administrator(){
        return "user/trap_administrator.html";
    }
    @GetMapping("/user/trap_freez")
    public String freez(){
        return "user/trap_freez.html";
    }
    
}