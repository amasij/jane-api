package com.jane;

import com.jane.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.mail.HtmlEmail;

import javax.inject.Named;
import java.util.Map;

@Named
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private final MailService mailService;

    @Override
    public boolean sendMail(String email, String template, Map<String, Object> bindings, String subject) {
        try {
            HtmlEmail htmlEmail = new HtmlEmail();
//            htmlEmail.setHtmlMsg(templateEngine.getAsString(template, bindings));
            htmlEmail.setMsg("Welcome to JANE")
                    .setSubject(subject)
                    .addTo(email);
            mailService.send(htmlEmail);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
