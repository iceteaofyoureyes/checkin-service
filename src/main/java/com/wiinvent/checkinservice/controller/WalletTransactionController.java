package com.wiinvent.checkinservice.controller;

import com.wiinvent.checkinservice.config.RequestContext;
import com.wiinvent.checkinservice.dto.CustomUserDetails;
import com.wiinvent.checkinservice.dto.response.BaseResponse;
import com.wiinvent.checkinservice.dto.response.WalletTransactionResponse;
import com.wiinvent.checkinservice.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class WalletTransactionController {

    private final WalletTransactionService service;

    @GetMapping("/point-reward-history")
    @PreAuthorize("hasAuthority('TRANSACTION.GET_POINT_REWARD_HISTORY')")
    public ResponseEntity<BaseResponse<Page<WalletTransactionResponse>>> getPointRewardHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @AuthenticationPrincipal CustomUserDetails user) {

        ZoneId zone = RequestContext.getZoneId();
        Page<WalletTransactionResponse> responses = service.getUserPointRewardHistory(user.getUsername(), zone, page, size, sort);

        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<Page<WalletTransactionResponse>>builder().
                        code(HttpStatus.OK.value()).
                        data(responses).
                        build()
                );
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('TRANSACTION.GET_SELF_TRANSACTIONS')")
    public ResponseEntity<BaseResponse<Page<WalletTransactionResponse>>> getUserTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort,
            @AuthenticationPrincipal CustomUserDetails user) {

        ZoneId zone = RequestContext.getZoneId();
        Page<WalletTransactionResponse> responses = service.getUserTransactions(user.getUsername(), zone, page, size, sort);

        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<Page<WalletTransactionResponse>>builder().
                        code(HttpStatus.OK.value()).
                        data(responses).
                        build()
                );
    }

}
