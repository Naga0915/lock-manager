package jp.oecu.lockmng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.oecu.lockmng.config.TimeZoneConfig;
import jp.oecu.lockmng.config.properties.LockConfig;
import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.service.ReservationService;
import jp.oecu.lockmng.util.CustomUserDetails;
import jp.oecu.lockmng.util.Result;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class ReserveController {
    private final ReservationService reservationService;
    private final TimeZoneConfig timeZoneConfig;
    private final LockConfig lockConfig;

    @Autowired
    public ReserveController(ReservationService reservationService, TimeZoneConfig timeZoneConfig, LockConfig lockConfig) {
        this.reservationService = reservationService;
        this.timeZoneConfig = timeZoneConfig;
        this.lockConfig = lockConfig;
    }

    @GetMapping("/user/resv")
    public String reserve() {
        return "user/reservation.html";
    }

    @PostMapping("/user/resv")
    public String reservePost(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute NewReserveModel newReserveModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/user/resv";
        }
        //サービスの処理
        if(userDetails == null){
            redirectAttributes.addFlashAttribute("msg", "ログインしてください");
            return "redirect:/user/resv";
        }
        Result<Boolean> result = reservationService.newReserve(newReserveModel, userDetails.getUser());
        if(result.isSuccess()){
            redirectAttributes.addFlashAttribute("msg", "予約完了しました");
        }else{
            redirectAttributes.addFlashAttribute("msg", result.getError());
        }
        return "redirect:/user/resv";
    }
    
    

    @GetMapping("/user/resv/get")
    public String reserveView(Model model, @RequestParam String year, @RequestParam String month, @RequestParam String day) {
        //return "reservation/reservation.html";
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        model.addAttribute("lockNum", lockConfig.getNum());
        return "fragments/resv_bar.html";
    }
}