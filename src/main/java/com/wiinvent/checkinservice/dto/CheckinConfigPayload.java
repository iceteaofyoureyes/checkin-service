package com.wiinvent.checkinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckinConfigPayload {
    /**
     * pointConfigs: danh sách điểm theo lần thứ N (index 0 -> lần 1)
     * Eg: [1,2,3,5,8,13,21]
     */
    private List<Integer> pointConfigs;
}
