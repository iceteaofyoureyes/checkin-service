package com.wiinvent.checkinservice.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private Boolean isFirstLogin;
    private Long expiryTime;

}
