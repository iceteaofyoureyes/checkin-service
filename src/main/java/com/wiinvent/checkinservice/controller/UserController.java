package com.wiinvent.checkinservice.controller;

import com.wiinvent.checkinservice.dto.request.CreateUserRequest;
import com.wiinvent.checkinservice.dto.response.BaseResponse;
import com.wiinvent.checkinservice.dto.response.UserResponse;
import com.wiinvent.checkinservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USER.CREATE')")
    public ResponseEntity<BaseResponse<UserResponse>> createUser(
            @Valid @ModelAttribute CreateUserRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        UserResponse response = userService.createUser(request, avatar);
        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(BaseResponse.<UserResponse>builder().
                        code(HttpStatus.CREATED.value()).
                        data(response).
                        build()
                );
    }
}
