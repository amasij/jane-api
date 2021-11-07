package com.jane.controllers;

import com.jane.ProductService;
import com.jane.dto.ProductCreationDto;
import com.jane.filter.ProductFilter;
import com.jane.pojo.ProductPojo;
import com.jane.pojo.QueryResultsPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductPojo> createProduct(@RequestBody @Valid ProductCreationDto dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @GetMapping("/search/{storeCode}")
    public ResponseEntity<QueryResultsPojo<ProductPojo>> searchProduct(ProductFilter filter, @PathVariable String storeCode) {
        return ResponseEntity.ok(productService.search(storeCode, filter));
    }
}
