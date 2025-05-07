package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserContentController {
    @GetMapping("/user")
    public String home(){
        return "user/home.html";
    }

    @GetMapping("/user/resv")
    public String viewResv(){
        return "user/resv.html";
    }

    @GetMapping("/user/lock")
    public String button() {
        return "user/button.html";
    }
}