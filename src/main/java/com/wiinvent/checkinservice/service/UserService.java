package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.request.CreateUserRequest;
import com.wiinvent.checkinservice.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse createUser(CreateUserRequest request, MultipartFile file);
    UserResponse getUserProfileByUsername(String username);
    UserResponse getUserProfileById(Long userId);
}
