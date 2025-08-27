package com.wiinvent.checkinservice.mapper;


import com.wiinvent.checkinservice.dto.WalletDTO;
import com.wiinvent.checkinservice.dto.request.CreateUserRequest;
import com.wiinvent.checkinservice.dto.response.UserResponse;
import com.wiinvent.checkinservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .wallets(user.getWallets().stream().map(w -> new WalletDTO(w.getWalletCode(), w.getBalance(), w.getWalletType().name())).toList())
                .build();
    }

    default User toUser(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();
    }
}
