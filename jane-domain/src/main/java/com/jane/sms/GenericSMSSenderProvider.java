package com.jane.sms;

import com.jane.sms.SMSSenderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GenericSMSSenderProvider implements SMSSender {

    private String[] recipients;
    String username = "";
    String password = "";
    String gateway = "";
    String encodedMsg = "";
    String sendingURL = "";
    private String from = "";
    private String message = "";

    GenericSMSSenderProvider() {
    }

    GenericSMSSenderProvider(String username, String password, String gateway){
        this.username = username;
        this.password = password;
        this.gateway = gateway;
    }

    @Override
    public void sendSms(String[] recipients, String message, String from) {
        this.recipients = recipients;
        this.message = message;
        this.from = from;
        send();
    }

    private void send() {
        System.out.println(">>>sending sms...");

        try {
            this.encodedMsg = URLEncoder.encode(this.message, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            System.err.println("SMS encoding failed");
            ex.printStackTrace();
        }

        String to = Arrays.toString(this.recipients).replace("[", "").replace("]", "").replace(" ", "");
        this.sendingURL = this.gateway + "username=" + this.username + "&password=" + this.password + "&sender=" + this.from + "&recipient=" + to + "&message=" + this.encodedMsg;
        if (SMSSenderFactory.loggingEnabled) {
            System.out.println("Full sms sending url ------------>>>>>>>" + this.sendingURL);
        }

        try {
            URL messageURL = new URL(this.sendingURL);

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(messageURL.openStream()));
                StringBuffer response = new StringBuffer();

                String inputLine;
                while((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }

                bufferedReader.close();
                System.out.println(response.toString());
            } catch (IOException ex) {
                System.err.println("IOException occurred while sending SMS");
                ex.printStackTrace();
            }
        } catch (MalformedURLException ex) {
            System.err.println("Malformed URL Exception, please contact sms service " +
                    "provider: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
}
