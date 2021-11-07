package com.jane;

import com.jane.mail.MailGunEmail;
import com.jane.mail.MailService;
import com.jane.retrofit.MailGunApi;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;
import java.util.Base64;

@RequiredArgsConstructor
public class MailServiceImpl implements MailService {


    public static final String NOTIFICATION_EMAIL_ADDRESS = "NOTIFICATION_EMAIL_ADDRESS";
    public static final String NOTIFICATION_EMAIL_PASSWORD = "NOTIFICATION_EMAIL_PASSWORD";
    public static final String EMAIL_SENDER_FROM_EMAIL = "EMAIL_SENDER_FROM_EMAIL";
    public static final String EMAIL_SENDER_FROM_NAME = "EMAIL_SENDER_FROM_NAME";
    public static final String MAIL_GUN_MESSAGES_API_KEY = "MAIL_GUN_MESSAGES_API_KEY";


    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SettingService settingService;

    private MailGunApi mailGunApi;

    @PostConstruct
    public void init() {
        this.mailGunApi = getMailgunApi();
    }

    @Async
    @Override
    public void send(Email email) {
        String emailAddress = settingService.getString(NOTIFICATION_EMAIL_ADDRESS, "simonjoseph750@gmail.com");
        String emailPassword = settingService.getString(NOTIFICATION_EMAIL_PASSWORD, "ssjj44rriill..");
        Integer smtpPort = settingService.getInteger("SMTP_PORT", 587);
        String smtpHost = settingService.getString("SMTP_HOST", "smtp.gmail.com");
        String fromEmail = settingService.getString(EMAIL_SENDER_FROM_EMAIL, "noreply@jane.com");
        String fromName = settingService.getString(EMAIL_SENDER_FROM_NAME, "JANE Inc");

        try {
            email.setHostName(smtpHost);
            email.setSmtpPort(smtpPort);
            email.setAuthenticator(new DefaultAuthenticator(emailAddress, emailPassword));
            email.setFrom(fromEmail, fromName);
            email.setSSLOnConnect(true);
            email.setStartTLSEnabled(true);
            email.send();
        } catch (EmailException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Async
    @Override
    public void sendViaMailgunApi(MailGunEmail mailGunEmail) {
        try {
            Response<ResponseBody> response = mailGunApi.sendMail(
                    mailGunEmail.getRecipientEmails(),
                    mailGunEmail.getHtmlMessage(),
                    mailGunEmail.getSubject())
                    .execute();
            if (!response.isSuccessful()) {
                logger.error("===> Mail sending to {} failed with code {} and message {} ",
                        String.join(", ", mailGunEmail.getRecipientEmails()), response.code(), response.errorBody() != null ? response.errorBody().string() : "null");
                throw new ApiCallException(response.raw().request().url().url().toString(),
                        response.code(),
                        response.message(),
                        response.errorBody().contentType().type());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MailGunApi getMailgunApi() {
        String mailgunUrl = settingService.getString("MAIL_GUN_MESSAGES_URL", "https://api.mailgun.net/v3/mg.dentaldoor.com/");
        if (!mailgunUrl.endsWith("/")) {
            mailgunUrl += "/";
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {

                    String mailgunApiKey = settingService.getString(MAIL_GUN_MESSAGES_API_KEY, "416574e9cf8f29bffce606b1bbae33ee");
                    String fromEmail = settingService.getString(EMAIL_SENDER_FROM_EMAIL, "noreply@dentaldoor.com");
                    String fromName = settingService.getString(EMAIL_SENDER_FROM_NAME, "DentalDoor Team");

                    Request.Builder newRequestBuilder = chain.request().newBuilder();
                    newRequestBuilder.addHeader("Authorization", "Bearer " + Base64.getEncoder().encodeToString(("api:" + mailgunApiKey).getBytes()));
                    newRequestBuilder.url(chain.request().url().newBuilder().addQueryParameter("from", String.format("%s <%s>", fromName, fromEmail)).build());
                    return chain.proceed(newRequestBuilder.build());
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mailgunUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(MailGunApi.class);
    }

    private static class ApiCallException extends com.jane.exception.ApiCallException {

        public ApiCallException(String url, int code, String message, String contentType) {
            super(url, code, message, contentType);
        }
    }

}
