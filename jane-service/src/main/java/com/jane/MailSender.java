package com.jane;

import java.util.Map;

public interface MailSender {
    boolean sendMail(String email, String template, Map<String, Object> bindings, String subject);
}
