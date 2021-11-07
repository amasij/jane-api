package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPasswordUpdateDto {
    private String oldPassword;
    private String newPassword;
}
