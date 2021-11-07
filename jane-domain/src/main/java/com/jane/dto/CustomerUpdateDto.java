package com.jane.dto;

import com.jane.enumeration.GenderConstant;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CustomerUpdateDto {
    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "First Name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Gender must be present")
    private GenderConstant gender;

    @NotBlank(message = "OTP must be present")
    private String otp;

    @NotBlank(message = "Phone number must be present")
    private String phoneNumber;
}
