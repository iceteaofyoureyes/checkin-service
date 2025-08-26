package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.response.WalletTransactionResponse;
import org.springframework.data.domain.Page;

import java.time.ZoneId;

public interface WalletTransactionService {
    Page<WalletTransactionResponse> getUserPointRewardHistory(String username, ZoneId userZone, int page, int size, String sortDirection);
    Page<WalletTransactionResponse> getUserTransactions(String username, ZoneId userZone, int page, int size, String sortDirection);
}
