package jp.oecu.lockmng.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ReservationInfoModel {
    private Boolean success;
    private String message;
    private List<Double[]> list;
}
