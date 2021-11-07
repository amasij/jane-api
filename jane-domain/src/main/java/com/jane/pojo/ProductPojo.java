package com.jane.pojo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductPojo {
    private String name;
    private String code;
    private BigDecimal price;
    private String description;
    private List<Long> images;
}
