package com.wiinvent.checkinservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeductPointsResponse {
    private boolean success;
    private String message;
    private long balance;
    private String txnRefCode;
}
