package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//管理者用ページなので一般ユーザはアクセスできない
@Controller
public class AdminController {
    @GetMapping("/admin")
    public String home(){
        return "admin/home.html";
    }

    @GetMapping("/admin/user")
    public String users(){
        return "admin/users.html";
    }

    @GetMapping("/admin/user/new")
    public String userNew(){
        return "admin/newuser.html";
    }
    
    @GetMapping("/admin/lock")
    public String lock(){
        return "admin/lock.html";
    }

    @GetMapping("/admin/resv")
    public String viewResv(){
        return "admin/resv.html";
    }
}
