package com.wiinvent.checkinservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionResponse {
    private long amount;
    private String txnType;
    private String refCode;
    private String createdAt;
}
