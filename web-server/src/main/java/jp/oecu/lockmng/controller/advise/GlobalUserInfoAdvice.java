package jp.oecu.lockmng.controller.advise;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jp.oecu.lockmng.controller.UserAccessController;
import jp.oecu.lockmng.util.CustomUserDetails;

@ControllerAdvice
public class GlobalUserInfoAdvice {

    private final UserAccessController userAccessController;

    GlobalUserInfoAdvice(UserAccessController userAccessController) {
        this.userAccessController = userAccessController;
    }
@ModelAttribute
    public void addUserInfoToModel(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            if(userDetails.getUser() != null){
                model.addAttribute("username", userDetails.getUser().getName());
            }else{
                model.addAttribute("username", "Guest");
            }
        }else{
            model.addAttribute("username", "Guest");
        }
    }
}
