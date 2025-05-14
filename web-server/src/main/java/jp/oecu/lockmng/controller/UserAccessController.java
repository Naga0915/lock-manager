package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class UserAccessController {
    @GetMapping("/login")
    public String login(HttpSession session) {
        session.setAttribute("session_id", 123);
        return "login.html";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("session_id");
        return "logout.html";
    }
}
