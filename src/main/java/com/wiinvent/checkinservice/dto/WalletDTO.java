package com.wiinvent.checkinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private String walletCode;
    private long balance;
    private String walletType;
}
