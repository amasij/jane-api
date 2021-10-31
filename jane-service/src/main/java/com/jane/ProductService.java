package com.jane;

import com.jane.dto.ProductCreationDto;
import com.jane.pojo.ProductPojo;

public interface ProductService {
    ProductPojo createProduct(ProductCreationDto dto);
}
