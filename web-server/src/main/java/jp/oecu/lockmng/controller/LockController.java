package jp.oecu.lockmng.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.oecu.lockmng.component.DeviceManager;
import jp.oecu.lockmng.model.LockRequestModel;
import jp.oecu.lockmng.service.DeviceService;
import jp.oecu.lockmng.util.CustomUserDetails;


@Controller
public class LockController {
    private final DeviceManager deviceManager;
    private final DeviceService deviceService;

    @Autowired
    public LockController(DeviceManager deviceManager, DeviceService deviceService) {
        this.deviceManager = deviceManager;
        this.deviceService = deviceService;
    }    

    @PostMapping("/lock")
    public String lockPost(@ModelAttribute LockRequestModel lockRequestModel, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        return "ok";
    }

    @PostMapping("/admin/lock")
    public String lockPostAdmin(@ModelAttribute @Valid LockRequestModel lockRequestModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", sb.toString());
            return "redirect:/admin/lock";
        }
        if(LockRequestModel.OP_LOCK.equals(lockRequestModel.getOperation())){
            Optional<String> result = deviceManager.tryLock(lockRequestModel.getLockId());
            if(result.isPresent()){
                redirectAttributes.addFlashAttribute("msg", result.get());
                return "redirect:/admin/lock";
            }
            redirectAttributes.addFlashAttribute("msg", String.format("鍵ID: %d を施錠しました", lockRequestModel.getLockId()));
            return "redirect:/admin/lock";
        }else if(LockRequestModel.OP_UNLOCK.equals(lockRequestModel.getOperation())){
            Optional<String> result = deviceManager.tryUnlock(lockRequestModel.getLockId());
            if(result.isPresent()){
                redirectAttributes.addFlashAttribute("msg", result.get());
                return "redirect:/admin/lock";
            }
            redirectAttributes.addFlashAttribute("msg", String.format("鍵ID: %d を解錠しました", lockRequestModel.getLockId()));
            return "redirect:/admin/lock";
        }else{
            redirectAttributes.addFlashAttribute("msg", String.format("無効な命令: %s", lockRequestModel.getOperation()));
            return "redirect:/admin/lock";
        }
    }
}
