package jp.oecu.lockmng.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.oecu.lockmng.config.properties.AppCommonConfig;
import jp.oecu.lockmng.config.properties.ReservationConfig;
import jp.oecu.lockmng.entity.NewRegister;
import jp.oecu.lockmng.entity.User;
import jp.oecu.lockmng.model.NewRegisterModel;
import jp.oecu.lockmng.model.NewUserModel;
import jp.oecu.lockmng.model.RegisterChangeStatModel;
import jp.oecu.lockmng.service.NewRegisterService;
import jp.oecu.lockmng.service.UserService;
import jp.oecu.lockmng.util.Result;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

//管理者用ページなので一般ユーザはアクセスできない
@Controller
public class AdminController {

    private final ReservationConfig reservationConfig;
    private final UserService userService;
    private final NewRegisterService newRegisterService;
    private final AppCommonConfig appCommonConfig;
    
    @Autowired
    public AdminController(UserService userService, NewRegisterService newRegisterService, AppCommonConfig appCommonConfig, ReservationConfig reservationConfig){
        this.userService = userService;
        this.newRegisterService = newRegisterService;
        this.appCommonConfig = appCommonConfig;
        this.reservationConfig = reservationConfig;
    }

    @GetMapping("/admin")
    public String home(){
        return "admin/home.html";
    }

    @GetMapping("/admin/user")
    public String users(Model model){
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users.html";
    }

    @GetMapping("/admin/user/new")
    public String userNew(Model model){
        return "admin/newuser.html";
    }

    @PostMapping("/admin/user/new")
    public String userNewPost(@ModelAttribute NewUserModel userModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/admin/user/new";
        }
        Optional<String> result = userService.newUser(userModel);
        if(result.isPresent()){
            redirectAttributes.addFlashAttribute("msg", result.get());
        }else{
            redirectAttributes.addFlashAttribute("msg", "新しいユーザを作成しました。");
        }
        return "redirect:/admin/user/new";
    }

    @GetMapping("/admin/register")
    public String register(@RequestParam(defaultValue = "0") String m, Model model) {
        List<NewRegister> list = newRegisterService.findAll();
        model.addAttribute("entries", list);
        model.addAttribute("baseurldayo", appCommonConfig.getBase_url());
        if(m.equals("1")){
            list.removeIf(li -> !li.isEnable());
        }
        return "admin/register.html";
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
    
    
    @GetMapping("/admin/lock")
    public String lock(){
        return "admin/lock.html";
    }

    @GetMapping("/admin/resv")
    public String viewResv(){
        return "admin/resv.html";
    }
}
