package com.jane.controllers;

import com.jane.AppRepository;
import com.jane.PhoneNumberService;
import com.jane.entity.Customer;
import com.jane.entity.QCustomer;
import com.jane.enums.ResourceValidationType;
import com.jane.exception.ErrorResponse;
import com.jane.filter.StoreFilter;
import com.jane.pojo.QueryResultsPojo;
import com.jane.pojo.StorePojo;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("resource")
public class ResourceController {
    private final AppRepository appRepository;
    private final PhoneNumberService phoneNumberService;

    @GetMapping("/validate-customer/{type}/{identifier}")
    public ResponseEntity<String> validate(@PathVariable("type") ResourceValidationType type,
                                           @PathVariable("identifier") String identifier) {

        JPAQuery<Customer> jpaQuery = appRepository.startJPAQuery(QCustomer.customer);
        switch (type) {
            case EMAIL:
                jpaQuery.where(QCustomer.customer.email.equalsIgnoreCase(identifier.trim()));
                break;
            case PHONE_NUMBER:
                jpaQuery.where(QCustomer.customer.phoneNumber.equalsIgnoreCase(phoneNumberService.formatPhoneNumber(identifier)));
        }
        if (jpaQuery.fetch().isEmpty()) {
            return ResponseEntity.ok("Valid");
        }
        throw new ErrorResponse(HttpStatus.BAD_REQUEST, type.name() + " already exists");
    }
}
