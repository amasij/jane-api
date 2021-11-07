package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductQuantityDto {
    @NotBlank
    private String productCode;

    @NotNull
    private Long quantity;

    private ProductSubQuantityDto substitute;
}
