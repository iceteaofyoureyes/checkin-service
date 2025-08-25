package com.wiinvent.checkinservice.controller;

import com.wiinvent.checkinservice.dto.request.AuthenticationRequest;
import com.wiinvent.checkinservice.dto.response.AuthenticationResponse;
import com.wiinvent.checkinservice.dto.response.BaseResponse;
import com.wiinvent.checkinservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<AuthenticationResponse>builder().
                        code(HttpStatus.OK.value()).
                        data(response).
                        build()
                );
    }
}
