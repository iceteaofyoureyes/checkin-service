package com.wiinvent.checkinservice.controller;

import com.wiinvent.checkinservice.config.RequestContext;
import com.wiinvent.checkinservice.dto.CustomUserDetails;
import com.wiinvent.checkinservice.dto.response.BaseResponse;
import com.wiinvent.checkinservice.dto.response.CheckinResponse;
import com.wiinvent.checkinservice.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        Long userId = user.getUserId();
        ZoneId zone = RequestContext.getZoneId();
        CheckinResponse response = checkinService.checkin(userId, zone);
        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<CheckinResponse>builder().
                        code(HttpStatus.OK.value()).
                        data(response).
                        build()
                );
    }
}
