package com.wiinvent.checkinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckinConfigDto {
    private boolean isActive;
    private int monthlyLimitDays;
    private CheckinConfigPayload payload;
    private List<TimeWindowDto> timeWindows;
    private OffsetDateTime updatedAt;
}
