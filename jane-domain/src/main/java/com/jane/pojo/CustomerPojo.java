package com.jane.pojo;

import com.jane.entity.Customer;
import com.jane.enumeration.GenderConstant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPojo {
    private String firstName;
    private String lastName;
    private String token;
    private String firebaseToken;
    private String email;
    private String phoneNumber;
    private Long id;
    private GenderConstant gender;

    public static CustomerPojo from(Customer customer) {
        CustomerPojo pojo = new CustomerPojo();
        pojo.setId(customer.getId());
        pojo.setEmail(customer.getEmail());
        pojo.setFirstName(customer.getFirstName());
        pojo.setGender(customer.getGender());
        pojo.setLastName(customer.getLastName());
        pojo.setPhoneNumber(customer.getPhoneNumber());
        return pojo;
    }
}
