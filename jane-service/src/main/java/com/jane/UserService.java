package com.jane;

import com.nimbusds.jose.JOSEException;

import java.util.Map;

public interface UserService {
    String createToken(String subject, Map<String, Object> claimSet);
    String decodeToken(String token);
}
