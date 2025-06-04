package jp.oecu.lockmng.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jp.oecu.lockmng.model.LockRequestModel;
import jp.oecu.lockmng.util.CustomUserDetails;


@Controller
public class LockController {
    @PostMapping("/unlock")
    public String unlockPost(@ModelAttribute LockRequestModel lockRequestModel, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        return "ok";
    }
    
}
