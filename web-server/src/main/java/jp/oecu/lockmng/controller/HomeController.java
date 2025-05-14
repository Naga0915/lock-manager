package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {
    @GetMapping("/")
    public String getMethodName(HttpSession session, Model model) {
        String name = "guest";
        if(session.getAttribute("session_id") != null){
            name = String.format("%d", (int)session.getAttribute("session_id"));
        }
        model.addAttribute("name", "["+name+"]");
        return "index.html";
    }
    @GetMapping("/header")
    public String getHeader(Model model) {
        return "fragments/header.html";
    }
}