package com.jane.notification;

import java.io.File;
import java.util.Map;


public interface EmailNotificationBuilder {

    EmailNotificationBuilder withBody(String body);

    EmailNotificationBuilder withBody(String ftl, Map<String, Object> bindings);

    EmailNotificationBuilder subject(String subject);

    EmailNotificationBuilder to(String... email);

    EmailNotificationBuilder from(String senderName);

    EmailNotificationBuilder addAttachment(File file);

    void send();
}
