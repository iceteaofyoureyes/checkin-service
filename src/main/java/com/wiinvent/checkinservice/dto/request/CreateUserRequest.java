package com.wiinvent.checkinservice.dto.request;

import com.wiinvent.checkinservice.validator.PasswordConstrain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must have 3-20 characters")
    private String username;

    @PasswordConstrain
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "User is required to be assigned at least 1 role")
    Set<String> roleNames;
}
