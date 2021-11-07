package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductCreationDto {

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Price cannot be blank")
    private BigDecimal price;

    @NotBlank(message = "Store code cannot be blank")
    private String storeCode;

    @NotNull(message = "Quantity cannot be blank")
    private Integer quantity;

    @NotBlank(message = "Product description cannot be blank")
    private String description;

    @NotNull(message = "Image IDs cannot be empty")
    @Size(min = 1,message = "Upload at least one image")
    private List<Long> imageIds;
}
