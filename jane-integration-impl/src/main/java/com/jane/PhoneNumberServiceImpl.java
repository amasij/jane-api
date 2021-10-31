package com.jane;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.StringUtils;

public class PhoneNumberServiceImpl implements PhoneNumberService {

    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public String formatPhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return null;
        }
        if(!phoneNumber.startsWith("+")){
            phoneNumber = "+234"+phoneNumber.trim();
        }
        return phoneNumber.trim();
//        try {
//            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber.replaceAll(" +", ""), "US");
//            return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
//        } catch (NumberParseException e) {
//            throw newven RuntimeException(e);
//        }
    }

    @Override
    public boolean isValid(String value) {

        if (value == null) {
            return true;
        }

        Phonenumber.PhoneNumber swissNumberProto;
        try {
            swissNumberProto = phoneNumberUtil.parse(value.trim(), "NG");
        } catch (NumberParseException e) {
            return false;
        }

        return phoneNumberUtil.isValidNumber(swissNumberProto);
    }

}



