package com.jane;

import com.jane.dto.ProductCreationDto;
import com.jane.filter.ProductFilter;
import com.jane.pojo.ProductPojo;
import com.jane.pojo.QueryResultsPojo;

public interface ProductService {
    ProductPojo createProduct(ProductCreationDto dto);
    QueryResultsPojo<ProductPojo> search(String storeCode,ProductFilter filter);
}
