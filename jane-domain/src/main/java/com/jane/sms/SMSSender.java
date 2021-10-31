package com.jane.sms;

public interface SMSSender {
    public void sendSms(String[] recipients, String message, String from);
}
