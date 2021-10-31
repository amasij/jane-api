package com.jane.sms;

import com.jane.sms.SMSSenderFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class VansoSMSSenderProvider implements SMSSender {

    private static final String CONTENT_TYPE = "text/xml; charset=iso-8859-1";
    private static final String ACCEPTED_LANGUAGES = "en-US,en;q=0.5";
    private static final String USER_AGENT = "Mozilla/5.0";
    private String username;
    private String password;
    private String gateway;
    public static String template = "<?xml version=\"1.0\"?><operation type=\"submit\"><account username=\"%s\" password=\"%s\"/><submitRequest> <deliveryReport>false</deliveryReport> <sourceAddress type=\"alphanumeric\">%s</sourceAddress> <destinationAddress type=\"international\">%s</destinationAddress> <text encoding=\"ISO-8859-1\">%s</text></submitRequest></operation>";

    VansoSMSSenderProvider(String username, String password, String gateway) {
        this.username = username;
        this.password = password;
        this.gateway = gateway;
    }

    @Override
    public void sendSms(String[] recipients, String message, String from) {

        for(int i = 0; i < recipients.length; ++i) {
            String recipient = recipients[i];

            try {
                String requestMessage = this.generateRequestBody(from, recipient, this.toHex(message));
                URL obj = new URL(this.gateway);
                HttpURLConnection con = (HttpURLConnection)obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setRequestProperty("Content-Type", "text/xml; charset=iso-8859-1");
                con.setRequestProperty("Content-Length", String.format(Locale.ENGLISH, "%d", requestMessage.getBytes().length));
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(requestMessage);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                if (SMSSenderFactory.loggingEnabled) {
                    System.out.println("\nSending 'POST' request to URL : " + this.gateway);
                    System.out.println("Post parameters : " + requestMessage);
                    System.out.println("Response Code : " + responseCode);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response = new StringBuffer();

                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                if (SMSSenderFactory.loggingEnabled) {
                    System.out.println(response.toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private String generateRequestBody(String source, String destination, String message) {
        return String.format(Locale.ENGLISH, template, this.username, this.password, source, destination, message);
    }

    private String toHex(String arg) throws UnsupportedEncodingException {
        return String.format("%x", new BigInteger(1, arg.getBytes("iso-8859-1")));
    }
}
