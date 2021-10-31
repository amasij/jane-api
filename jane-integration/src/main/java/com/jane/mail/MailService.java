package com.jane.mail;

import org.apache.commons.mail.Email;

public interface MailService {

    void send(Email email);

    void sendViaMailgunApi(MailGunEmail mailGunEmail);
}
