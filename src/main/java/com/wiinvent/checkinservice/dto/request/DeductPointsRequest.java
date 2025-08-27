package com.wiinvent.checkinservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductPointsRequest {
    @Min(value = 1)
    private Long userId;
    @Min(value = 1)
    private long amount;
    @NotBlank(message = "txnRefCode is Required and not empty")
    private String txnRefCode;
}
