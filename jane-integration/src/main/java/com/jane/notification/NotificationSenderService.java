package com.jane.notification;

public interface NotificationSenderService {
    void sendSms(String message,String from , String... recipients);
}
