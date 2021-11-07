package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class DeliveryCostEstimateDto {
    @NotNull
    private List<String> productCodes;
    @NotBlank
    private String storeCode;
    @NotBlank
    private String latitude;
    @NotBlank
    private String longitude;
}
