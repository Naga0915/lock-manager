package jp.oecu.lockmng.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.util.CustomUserDetails;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReserveController {
    @GetMapping("/resv")
    public String reserve(@RequestParam String month, @RequestParam String year) {
        return "user/reservation.html";
    }

    @PostMapping("/resv")
    public String reservePost(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute NewReserveModel newReserveModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/resv";
        }
        //サービスの処理
        return "redirect:/resv";
    }
}