package com.wiinvent.checkinservice.service.impl;

import com.wiinvent.checkinservice.dto.request.CreateUserRequest;
import com.wiinvent.checkinservice.dto.response.UserResponse;
import com.wiinvent.checkinservice.entity.Role;
import com.wiinvent.checkinservice.entity.User;
import com.wiinvent.checkinservice.exception.AppException;
import com.wiinvent.checkinservice.exception.ErrorCode;
import com.wiinvent.checkinservice.exception.ResourceNotFoundException;
import com.wiinvent.checkinservice.mapper.UserMapper;
import com.wiinvent.checkinservice.repository.RoleRepository;
import com.wiinvent.checkinservice.repository.UserRepository;
import com.wiinvent.checkinservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request, MultipartFile avatar) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "This username is not available");
        }

        Set<String> roleNames = request.getRoleNames();
        Set<Role> roles = roleRepository.findByRoleNameIn(roleNames);

        if (roles.size() != roleNames.size()) {
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "There are some roles that not exist");
        }

        String avatarUrl = fileService.storeAvatarFile(avatar);
        User user = userMapper.toUser(request);
        user.setAvatarUrl(avatarUrl);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }
}
