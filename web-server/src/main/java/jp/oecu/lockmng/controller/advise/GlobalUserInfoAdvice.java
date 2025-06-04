package jp.oecu.lockmng.controller.advise;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.oecu.lockmng.util.CustomUserDetails;

@ControllerAdvice
public class GlobalUserInfoAdvice {
@ModelAttribute
    public void addUserInfoToModel(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUser().getName());
        }
    }
}
