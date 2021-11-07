package com.jane.pojo;

import com.jane.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSubPojo {
    private Product product;
    private Product substitute;
    private Long quantity;
}
