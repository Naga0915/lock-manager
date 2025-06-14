package jp.oecu.lockmng.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.service.NewRegisterService;
import jp.oecu.lockmng.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegisterController {
    private final NewRegisterService newRegisterService;
    private final UserService userService;

    public RegisterController(NewRegisterService newRegisterService, UserService userService){
        this.newRegisterService = newRegisterService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(@RequestParam(defaultValue = "null") String id, Model model) {
        if(!newRegisterService.isValid(id)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Registration Id");
        }
        model.addAttribute("id", id);
        return "/register/register.html";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute NewUserModel userModel, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if(!newRegisterService.isValid(userModel.getRegistrationId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Registration Id");
        }
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/register";
        }
        Optional<String> result = userService.newUser(userModel);
        if(result.isPresent()){
            redirectAttributes.addFlashAttribute("msg", result.get());
            return "redirect:/register";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            // X-Forwarded-Forはカンマ区切りで複数IPが来ることがあるので先頭を使う
            ip = ip.split(",")[0].trim();
        }
        newRegisterService.setStatusById(userModel.getRegistrationId(), false, request.getHeader("User-Agent"), ip, userModel.getUserId());

        return "redirect:/register/success";
    }
    
    @GetMapping("/register/success")
    public String success() {
        return "/register/success.html";
    }
}
