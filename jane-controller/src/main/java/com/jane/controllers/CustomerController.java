package com.jane.controllers;

import com.jane.CustomerService;
import com.jane.dto.CustomerCreationDto;
import com.jane.dto.CustomerLoginDto;
import com.jane.pojo.CustomerPojo;
import com.jane.security.constraint.Public;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("customer")
public class CustomerController {
    private final CustomerService customerService;

    @Public
    @PostMapping("/register")
    public ResponseEntity<CustomerPojo> createCustomer(@RequestBody @Valid CustomerCreationDto dto) {
        return ResponseEntity.ok(customerService.registerCustomer(dto));
    }

    @Public
    @PostMapping("/login")
    public ResponseEntity<CustomerPojo> login(@RequestBody @Valid CustomerLoginDto dto) {
        return ResponseEntity.ok(customerService.loginCustomer(dto));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<CustomerPojo> getCustomer(@PathVariable("id") Long id){
        return ResponseEntity.ok(customerService.getCustomer(id));
    }
}
