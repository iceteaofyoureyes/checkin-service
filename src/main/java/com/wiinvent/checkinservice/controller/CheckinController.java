package com.wiinvent.checkinservice.controller;

import com.wiinvent.checkinservice.config.RequestContext;
import com.wiinvent.checkinservice.dto.CustomUserDetails;
import com.wiinvent.checkinservice.dto.response.BaseResponse;
import com.wiinvent.checkinservice.dto.response.CheckinResponse;
import com.wiinvent.checkinservice.dto.response.MonthCheckinResponse;
import com.wiinvent.checkinservice.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;

@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @PostMapping
    @PreAuthorize("hasAuthority('CHECKIN.CHECK')")
    public ResponseEntity<BaseResponse<CheckinResponse>> checkin(@AuthenticationPrincipal CustomUserDetails user) {
        ZoneId zone = RequestContext.getZoneId();
        CheckinResponse response = checkinService.checkin(user.getUsername(), zone);
        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<CheckinResponse>builder().
                        code(HttpStatus.OK.value()).
                        data(response).
                        build()
                );
    }

    @GetMapping("/month-status")
    @PreAuthorize("hasAuthority('CHECKIN.VIEW_MONTH_STATUS')")
    public ResponseEntity<BaseResponse<MonthCheckinResponse>> getMonthStatus(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        ZoneId zone = RequestContext.getZoneId();

        MonthCheckinResponse resp = checkinService.getMonthCheckins(user.getUsername(), zone);

        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<MonthCheckinResponse>builder().
                        code(HttpStatus.OK.value()).
                        data(resp).
                        build()
                );
    }
}
