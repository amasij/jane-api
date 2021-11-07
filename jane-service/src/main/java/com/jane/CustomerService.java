package com.jane;

import com.jane.dto.CustomerCreationDto;
import com.jane.dto.CustomerLoginDto;
import com.jane.dto.CustomerPasswordUpdateDto;
import com.jane.dto.CustomerUpdateDto;
import com.jane.pojo.CustomerPojo;

public interface CustomerService {
    CustomerPojo registerCustomer(CustomerCreationDto dto);
    CustomerPojo loginCustomer(CustomerLoginDto dto);
    CustomerPojo updateCustomer(Long customerId, CustomerUpdateDto dto);
    CustomerPojo updateCustomerPassword(Long customerId, CustomerPasswordUpdateDto dto);
    CustomerPojo getCustomer(Long id);
}
