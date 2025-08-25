package com.wiinvent.checkinservice.service.impl;

import com.wiinvent.checkinservice.dto.request.AuthenticationRequest;
import com.wiinvent.checkinservice.dto.response.AuthenticationResponse;
import com.wiinvent.checkinservice.entity.User;
import com.wiinvent.checkinservice.exception.AuthenticationException;
import com.wiinvent.checkinservice.repository.UserRepository;
import com.wiinvent.checkinservice.service.AuthenticationService;
import com.wiinvent.checkinservice.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Username or password is wrong") {
                });

        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AuthenticationException("Username or password is wrong");
        }

        JwtUtils.TokenInfo accessToken = jwtUtils.generateToken(user);
        //todo implement refresh token

        return AuthenticationResponse.builder()
                .accessToken(accessToken.token())
                .expiryTime(accessToken.expiryTime())
                .build();
    }
}
