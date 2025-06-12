package jp.oecu.lockmng.controller;

import java.util.List;

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
import jp.oecu.lockmng.entity.Reservation;
import jp.oecu.lockmng.model.NewReserveModel;
import jp.oecu.lockmng.service.ReservationService;
import jp.oecu.lockmng.util.CustomUserDetails;
import jp.oecu.lockmng.util.Result;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReserveController {
    private final ReservationService reservationService;
    private final TimeZoneConfig timeZoneConfig;

    @Autowired
    public ReserveController(ReservationService reservationService, TimeZoneConfig timeZoneConfig) {
        this.reservationService = reservationService;
        this.timeZoneConfig = timeZoneConfig;
    }

    @GetMapping("/resv")
    public String reserve(Model model) {
        Result<List<Reservation>> result = reservationService.findAllUTC();
        if(result.isSuccess()){
            List<Reservation> list = result.getValue();
            for (Reservation r : list) {
                r.setStartTimeUtc(r.getStartTimeUtc().withZoneSameInstant(timeZoneConfig.getZoneId()));
                r.setEndTimeUtc(r.getEndTimeUtc().withZoneSameInstant(timeZoneConfig.getZoneId()));
            }
            model.addAttribute("list", list);
        }else{
            model.addAttribute("msg", result.getError());
        }
        return "admin/resv.html";
    }

    @PostMapping("/resv")
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
        if(userDetails == null){
            redirectAttributes.addFlashAttribute("msg", "ログインしてください");
            return "redirect:/test/1";
        }
        Result<Boolean> result = reservationService.newReserve(newReserveModel, userDetails.getUser());
        if(result.isSuccess()){
            redirectAttributes.addFlashAttribute("msg", "予約完了しました");
        }else{
            redirectAttributes.addFlashAttribute("msg", result.getError());
        }
        return "redirect:/test/1";
    }
}