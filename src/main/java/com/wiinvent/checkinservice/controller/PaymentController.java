package com.wiinvent.checkinservice.controller;

import com.wiinvent.checkinservice.dto.request.DeductPointsRequest;
import com.wiinvent.checkinservice.dto.response.BaseResponse;
import com.wiinvent.checkinservice.dto.response.DeductPointsResponse;
import com.wiinvent.checkinservice.dto.response.WalletTransactionResponse;
import com.wiinvent.checkinservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/simulate-payment")
    @PreAuthorize("hasAuthority('PAYMENT.SIMULATE_PAYMENT')")
    public ResponseEntity<BaseResponse<DeductPointsResponse>> simulatePayment(
            @RequestBody @Valid DeductPointsRequest request) {

        DeductPointsResponse response = service.simulatePayment(request);
        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<DeductPointsResponse>builder().
                        code(HttpStatus.OK.value()).
                        data(response).
                        build()
                );
    }
}
