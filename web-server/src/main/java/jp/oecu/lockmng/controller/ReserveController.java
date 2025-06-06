package jp.oecu.lockmng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ReserveController {
    @GetMapping("/resv")
    public String reserve(@RequestParam String month, @RequestParam String year) {
        return "user/reservation.html";
    }

    @PostMapping("/resv")
    public String reservePost(@RequestBody String entity) {
        
        return entity;
    }
}
