package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.request.DeductPointsRequest;
import com.wiinvent.checkinservice.dto.response.DeductPointsResponse;

public interface PaymentService {
    public DeductPointsResponse simulatePayment(DeductPointsRequest request);
}
