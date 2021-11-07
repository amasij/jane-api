package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class OrderCreationDto {
    @NotNull List< @Valid ProductQuantityDto> productsQuantities;
    @NotNull
    private Long deductibleHashId;
    @NotBlank
    private String longitude;
    @NotBlank
    private String latitude;
    @NotBlank
    private String stateCode;
    @NotBlank
    private String addressDescription;
    @NotBlank
    private String recipientName;
    @NotBlank
    private String recipientPhoneNumber;
    private String instructions;
}
