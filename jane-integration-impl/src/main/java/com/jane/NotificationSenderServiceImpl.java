package com.jane;


import com.jane.notification.NotificationSenderService;
import com.jane.service.AppConfigurationProperties;
import com.jane.sms.SMSSender;
import com.jane.sms.SMSSenderFactory;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Objects;

@RequiredArgsConstructor
public class NotificationSenderServiceImpl implements NotificationSenderService {

    private final Environment environment;

    private SMSSender smsSender;

    private final AppConfigurationProperties appConfigurationProperties;

    private String TWILIO_SENDER;

    //Values are TWILIO, VANSO ...
    private String SMS_PROVIDER;

    private final SettingService settingService;

    @PostConstruct
    public void init() {
        SMS_PROVIDER = settingService.getString("SMS_PROVIDER", "VANSO");

        SMSSenderFactory.loggingEnabled = true;
        if (environment.getProperty("sms.provider", "VANSO").equalsIgnoreCase("VANSO")) {
            try {
                smsSender = SMSSenderFactory.get(
                        Objects.requireNonNull(environment.getProperty("sms.vanso.username")),
                        Objects.requireNonNull(environment.getProperty("sms.vanso.password")),
                        Objects.requireNonNull(environment.getProperty("sms.vanso.url")),
                        SMSSenderFactory.SMSProvider.VANSO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Twilio.init(appConfigurationProperties.getTwilioAccountSSID(), appConfigurationProperties.getTwilioAuthToken());
        TWILIO_SENDER = settingService.getString("TWILIO_SENDER", "+12163500912");
    }

    @Override
    public void sendSms(String message, String from, String... recipients) {
        if (SMS_PROVIDER.equalsIgnoreCase("TWILIO")) {
            for (String recipient : recipients) {
                Message.creator(new PhoneNumber(recipient), // to
                        new PhoneNumber(TWILIO_SENDER), // from
                        message)
                        .create();
            }
        } else if (SMS_PROVIDER.equalsIgnoreCase("VANSO")) {
            smsSender.sendSms(recipients, message, from);
        } else {
            smsSender.sendSms(recipients, message, from);
        }
    }

}

