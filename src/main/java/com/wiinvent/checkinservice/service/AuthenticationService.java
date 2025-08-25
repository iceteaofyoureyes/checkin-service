package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.request.AuthenticationRequest;
import com.wiinvent.checkinservice.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
