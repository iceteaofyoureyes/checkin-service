package com.wiinvent.checkinservice.dto.response;

import com.wiinvent.checkinservice.dto.CheckinDayDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthCheckinResponse {
    List<CheckinDayDTO> monthCheckins;
}
