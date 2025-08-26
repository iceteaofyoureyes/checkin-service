package com.wiinvent.checkinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent time window like {"start":"09:00", "end":"11:00"}
 * Note: strings are HH:mm, processed by LocalTime in service when needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeWindowDto {
    private String start;
    private String end;
}
