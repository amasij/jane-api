package com.jane.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app", ignoreInvalidFields = true)
@Getter
@Setter
public class AppConfigurationProperties {
    private String authBaseUrl;
    private String frontendBaseUrl;
    /**
     * Path to the directory where to find email templates
     */
    private String emailTemplatesDirectory;
    private String twilioAccountSSID;

    private String twilioAuthToken;

    private String firebaseDynamicLinkBaseUrl;
    private String firebaseApiKey;
    private String dynamicLinksDomainUriPrefix;
    private String metabaseSiteUrl;
    private String metabaseSecretKey;
    /**
     * This is the secret key to be used for all aes encryptions
     */
    private String encryptionSecretKey;
    private String hashSalt;
}
