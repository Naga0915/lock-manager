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
import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.service.ReservationService;
import jp.oecu.lockmng.util.CustomUserDetails;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ReserveController {
    private final ReservationService reservationService;
    private final TimeZoneConfig timeZoneConfig;

    @Autowired
    public ReserveController(ReservationService reservationService, TimeZoneConfig timeZoneConfig) {
        this.reservationService = reservationService;
        this.timeZoneConfig = timeZoneConfig;
    }

    @GetMapping("/user/resv")
    public String reserve(Model model) {
        return "reservation/reservation.html";
    }

    @GetMapping("/user/resv/get")
    public String reserveView(Model model) {
        return "reservation/view_reservation.html";
    }
    

    @PostMapping("/user/resv")
    public String reservePost(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute NewReserveModel newReserveModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            for (FieldError e : bindingResult.getFieldErrors()) {
                sb.append(e.getDefaultMessage() + "\n");
            }
            redirectAttributes.addFlashAttribute("msg", bindingResult);
            return "redirect:/test/1";
        }
        //サービスの処理
        return "redirect:/";
    }
}