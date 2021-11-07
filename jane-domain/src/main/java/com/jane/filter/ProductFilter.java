package com.jane.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilter {
    private Long priceMax;
    private Long priceMin;
    private Long offset;
    private Long limit;
    private String name;
}
