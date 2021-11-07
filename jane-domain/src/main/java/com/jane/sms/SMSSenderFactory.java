package com.jane.sms;

import java.util.Arrays;

public class SMSSenderFactory {

    public enum SMSProvider {
        GENERIC, VANSO
    }

    public static boolean loggingEnabled = false;

    private SMSSenderFactory() {
    }

    public static SMSSender get(String username, String password, String gateway) throws Exception {
        return get(username, password, gateway, SMSProvider.GENERIC);
    }

    public static SMSSender get(String username, String password, String gateway, SMSProvider provider) throws Exception {

        if (username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                gateway != null && !gateway.trim().isEmpty() ) {
            switch (provider){
                case VANSO:
                    return new VansoSMSSenderProvider(username, password, gateway);
                case GENERIC:
                    return new GenericSMSSenderProvider(username, password, gateway);
                default:
                    throw new IllegalArgumentException("Please provide a supported Provider value: "
                            + Arrays.toString(SMSProvider.values()));
            }
        } else {
            throw new Exception("Please provide values for username, password, gateway");
        }
    }
}
