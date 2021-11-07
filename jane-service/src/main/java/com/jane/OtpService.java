package com.jane;

public interface OtpService {
    boolean sendOtp(String phoneNumber);
    boolean validateOtp(String phoneNUmber, String code);
}
