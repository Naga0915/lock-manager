package jp.oecu.lockmng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import jp.oecu.lockmng.model.ReservationInfoModel;
import jp.oecu.lockmng.model.ReservationInfoRequestModel;
import jp.oecu.lockmng.service.ReservationService;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class ApiController {
    private final ReservationService reservationService;

    @Autowired
    public ApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/api/resv/get")
    public String getResvInfo(@RequestAttribute ReservationInfoRequestModel model) {
        ReservationInfoModel result;
        return "todo";
    }
}
