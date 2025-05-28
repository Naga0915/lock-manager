package jp.oecu.lockmng.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.service.UserService;


//管理者用ページなので一般ユーザはアクセスできない
@Controller
public class AdminController {
    private final UserService userService;
    
    @Autowired
    public AdminController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String home(){
        return "admin/home.html";
    }

    @GetMapping("/admin/user")
    public String users(){
        return "admin/users.html";
    }

    @GetMapping("/admin/user/new")
    public String userNew(Model model){
        //model.addAttribute("msg", "ヤッターマン");
        return "admin/newuser.html";
    }

    @PostMapping("/admin/user/new")
    public String userNewPost(@ModelAttribute NewUserModel userModel, Model model, RedirectAttributes redirectAttributes) {
        Optional<String> result = userService.newUser(userModel);
        if(result.isPresent()){
            redirectAttributes.addFlashAttribute("msg", result.get());
        }else{
            redirectAttributes.addFlashAttribute("msg", "successfully created new user");
        }
        return "redirect:/admin/user/new";
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
