package com.jane;

import lombok.RequiredArgsConstructor;

import javax.inject.Named;

@Named
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService{
    @Override
    public boolean sendOtp(String phoneNumber) {
        return true;
    }

    @Override
    public boolean validateOtp(String phoneNUmber, String code) {
        return true;
    }
}
