package com.jane;

import com.jane.dto.CustomerCreationDto;
import com.jane.dto.CustomerLoginDto;
import com.jane.pojo.CustomerPojo;

public interface CustomerService {
    CustomerPojo registerCustomer(CustomerCreationDto dto);
    CustomerPojo loginCustomer(CustomerLoginDto dto);
    CustomerPojo getCustomer(Long id);
}
