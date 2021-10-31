package com.jane;

import com.jane.constants.Constants;
import com.jane.dto.CustomerCreationDto;
import com.jane.dto.CustomerLoginDto;
import com.jane.entity.Customer;
import com.jane.entity.CustomerDetail;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.mail.MailService;
import com.jane.pojo.CustomerPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final SettingService settingService;
    private final CustomerDetailRepository customerDetailRepository;
    private final UserService userService;
    private final MailSender mailSender;
    @Inject
    private Provider<Customer> loggedInCustomer;

    @Transactional
    @Override
    public CustomerPojo registerCustomer(CustomerCreationDto dto) {
        if (customerRepository.findActiveByEmail(dto.getEmail()).isPresent()) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        String salt = settingService.getString(Constants.HASH_SALT, BCrypt.gensalt());
        CustomerDetail customerDetail = createCustomerDetail(dto);
        Customer customer = new Customer();
        customer.setStatus(GenericStatusConstant.ACTIVE);
        customer.setEmail(dto.getEmail());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPassword(BCrypt.hashpw(dto.getPassword(), salt));
        customer.setDetail(customerDetail);
        Customer savedCustomer = customerRepository.save(customer);
        sendWelcomeEmail(customer);
        return getPojo(savedCustomer, customerDetail);
    }

    private void sendWelcomeEmail(Customer customer){
        mailSender.sendMail(customer.getEmail(),null,null,"Welcome to JANE");
    }

    @Override
    public CustomerPojo loginCustomer(CustomerLoginDto dto) {
        Optional<Customer> optionalCustomer = customerRepository.findActiveByEmail(dto.getEmail().toLowerCase());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if (BCrypt.checkpw(dto.getPassword(), customer.getPassword())) {
                return getPojo(customer, customer.getDetail());
            }
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        throw new ErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    @Override
    public CustomerPojo getCustomer(Long id) {
        return getPojo(loggedInCustomer.get(), loggedInCustomer.get().getDetail());
    }

    private CustomerPojo getPojo(Customer customer, CustomerDetail detail) {
        CustomerPojo pojo = CustomerPojo.from(customer);
        pojo.setFirebaseToken(detail.getFirebaseToken());
        pojo.setToken(userService.createToken(customer.getId().toString(), new HashMap<>()));
        return pojo;
    }

    private CustomerDetail createCustomerDetail(CustomerCreationDto dto) {
        CustomerDetail customerDetail = new CustomerDetail();
        customerDetail.setFirebaseToken(dto.getFirebaseToken());
        Optional.ofNullable(dto.getDeviceId()).ifPresent(x -> customerDetail.getDeviceId());
        return customerDetailRepository.save(customerDetail);
    }
}
