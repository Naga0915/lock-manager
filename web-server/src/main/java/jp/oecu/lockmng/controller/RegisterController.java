package jp.oecu.lockmng.controller;

import java.util.List;
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
import jp.oecu.lockmng.config.properties.AppCommonConfig;
import jp.oecu.lockmng.entity.NewRegister;
import jp.oecu.lockmng.model.NewRegisterModel;
import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.model.RegisterChangeStatModel;
import jp.oecu.lockmng.service.NewRegisterService;
import jp.oecu.lockmng.service.UserService;
import jp.oecu.lockmng.util.Result;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
    private final NewRegisterService newRegisterService;
    private final UserService userService;
    private final AppCommonConfig appCommonConfig;

    public RegisterController(NewRegisterService newRegisterService, UserService userService, AppCommonConfig appCommonConfig){
        this.newRegisterService = newRegisterService;
        this.userService = userService;
        this.appCommonConfig = appCommonConfig;
    }

    @GetMapping("/register")
    public String registerUser(@RequestParam(defaultValue = "null") String id, Model model) {
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
            return "redirect:/register?id=" + userModel.getRegistrationId();
        }
        Optional<String> result = userService.newUser(userModel);
        if(result.isPresent()){
            redirectAttributes.addFlashAttribute("msg", result.get());
            return "redirect:/register?id=" + userModel.getRegistrationId();
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

    @PostMapping("/admin/register/new")
    public String registerNewPost(@ModelAttribute NewRegisterModel newRegisterModel, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/admin/register";
        }
        Result<String> result = newRegisterService.getNewRegister(newRegisterModel);
        if(!result.isSuccess()){
            redirectAttributes.addFlashAttribute("msg", result.getError());
        }else{
            redirectAttributes.addFlashAttribute("msg", "一時URLを発行しました。URL: " + appCommonConfig.getBase_url() + "/register?id=" + result.getValue());
        }
        return "redirect:/admin/register";
    }

    @PostMapping("/admin/register/stat")
    public String registerStatPost(@ModelAttribute RegisterChangeStatModel registerChangeStatModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/admin/register";
        }
        if(!newRegisterService.existsByUuid(registerChangeStatModel.getUuid())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Registration Id");
        }
        Optional<String> result = newRegisterService.setStatusById(registerChangeStatModel.getUuid(), registerChangeStatModel.isEnabled());
        if(result.isPresent()){
            redirectAttributes.addFlashAttribute("msg", result.get());
            return "redirect:/admin/register";
        }
        redirectAttributes.addFlashAttribute("msg", "一時URLを" + (registerChangeStatModel.isEnabled() ? "有効" : "無効") + "にしました。ID: " + registerChangeStatModel.getUuid());
        return "redirect:/admin/register";
    }

    @GetMapping("/admin/register")
    public String registerAdmin(@RequestParam(defaultValue = "0") String m, Model model) {
        List<NewRegister> list = newRegisterService.findAll();
        model.addAttribute("entries", list);
        model.addAttribute("baseurldayo", appCommonConfig.getBase_url());
        if(m.equals("1")){
            list.removeIf(li -> !li.isEnable());
        }
        return "admin/register.html";
    }
}
