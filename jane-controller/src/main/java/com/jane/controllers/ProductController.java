package com.jane.controllers;

import com.jane.ProductService;
import com.jane.dto.ProductCreationDto;
import com.jane.dto.StoreCreationDto;
import com.jane.pojo.ProductPojo;
import com.jane.pojo.StoreCreationPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductPojo> createStore(@RequestBody @Valid ProductCreationDto dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }
}
