package com.jane;

import com.jane.constants.Constants;
import com.jane.dto.CustomerCreationDto;
import com.jane.dto.CustomerLoginDto;
import com.jane.dto.CustomerPasswordUpdateDto;
import com.jane.dto.CustomerUpdateDto;
import com.jane.entity.Customer;
import com.jane.entity.CustomerDetail;
import com.jane.entity.QCustomer;
import com.jane.enumeration.CustomerTypeConstant;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.pojo.CustomerPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final SettingService settingService;
    private final CustomerDetailRepository customerDetailRepository;
    private final UserService userService;
    private final MailSender mailSender;
    private final AppRepository appRepository;
    private final PhoneNumberService phoneNumberService;
    private final OtpService otpService;

    private String getSalt() {
        return settingService.getString(Constants.HASH_SALT, BCrypt.gensalt());
    }

    @Transactional
    @Override
    public CustomerPojo registerCustomer(CustomerCreationDto dto) {
        if (customerRepository.findActiveByEmail(dto.getEmail()).isPresent()) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        CustomerDetail customerDetail = createCustomerDetail(dto);
        Customer customer = new Customer();
        customer.setStatus(GenericStatusConstant.ACTIVE);
        customer.setEmail(dto.getEmail());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPassword(BCrypt.hashpw(dto.getPassword(), getSalt()));
        customer.setDetail(customerDetail);
        customer.setType(CustomerTypeConstant.CUSTOMER);
        Customer savedCustomer = customerRepository.save(customer);
        sendWelcomeEmail(customer);
        return getPojo(savedCustomer, customerDetail);
    }

    private void sendWelcomeEmail(Customer customer) {
        mailSender.sendMail(customer.getEmail(), null, null, "Welcome to JANE");
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

    @Transactional
    @Override
    public CustomerPojo updateCustomer(Long customerId, CustomerUpdateDto dto) {
        String phoneNumber = phoneNumberService.formatPhoneNumber(dto.getPhoneNumber());
        if (!otpService.validateOtp(phoneNumber, dto.getOtp())) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        Customer customer = getCustomerById(customerId);
        customer.setEmail(dto.getEmail());
        customer.setGender(dto.getGender());
        customer.setPhoneNumber(phoneNumber);
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        return updated(customer);
    }

    private CustomerPojo updated(Customer customer) {
        Customer updatedCustomer = appRepository.persist(customer);
        return getPojo(updatedCustomer, updatedCustomer.getDetail());
    }

    @Transactional
    @Override
    public CustomerPojo updateCustomerPassword(Long customerId, CustomerPasswordUpdateDto dto) {
        Customer customer = getCustomerById(customerId);
        if (!BCrypt.checkpw(dto.getOldPassword(), customer.getPassword())) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect Password");
        }
        customer.setPassword(BCrypt.hashpw(dto.getNewPassword(), getSalt()));
        return updated(customer);
    }

    private Customer getCustomerById(Long customerId) {
        return customerRepository.findActiveById(customerId)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Customer does not exist"));
    }

    @Override
    public CustomerPojo getCustomer(Long id) {
        List<Customer> customers = appRepository.startJPAQuery(QCustomer.customer)
                .innerJoin(QCustomer.customer.detail).fetchJoin()
                .where(QCustomer.customer.id.eq(id))
                .where(QCustomer.customer.status.eq(GenericStatusConstant.ACTIVE))
                .fetch();
        if (customers.isEmpty()) {

            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Customer does not exist");
        }
        Customer customer = customers.get(0);
        return getPojo(customer, customer.getDetail());
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
