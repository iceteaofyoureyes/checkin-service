package com.wiinvent.checkinservice.dto.response;

import com.wiinvent.checkinservice.dto.WalletDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String avatarUrl;
    private List<WalletDTO> wallets;
}
