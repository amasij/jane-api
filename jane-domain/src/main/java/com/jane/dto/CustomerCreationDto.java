package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CustomerCreationDto {
    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "First Name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @Size(message = "Password cannot be less than 6 characters", min = 6)
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "FirebaseToken is required")
    private String firebaseToken;

    private String deviceId;
}
