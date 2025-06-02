package jp.oecu.lockmng.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jp.oecu.lockmng.service.NewRegisterService;


@Controller
public class RegisterController {
    private final NewRegisterService newRegisterService;

    public RegisterController(NewRegisterService newRegisterService){
        this.newRegisterService = newRegisterService;
    }

    @GetMapping("/register")
    public String register(@RequestParam(defaultValue = "null") String id) {
        if(!newRegisterService.isValid(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "登録IDが無効です");
        }
        return "/register/register.html";
    }

    @GetMapping("/register/success")
    public String success() {
        return "/register/success.html";
    }
    
    
}
