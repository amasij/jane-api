package com.jane;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jane.exception.ErrorResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Named
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final SettingService settingService;
    Algorithm algorithm;
    private String TOKEN_SECRET;
    private String HOST;

    @PostConstruct
    private void init(){
        TOKEN_SECRET = settingService.getString("TOKEN_SECRET", BCrypt.gensalt());
        HOST = settingService.getString("HOST", "localhost");
        algorithm = Algorithm.HMAC256(TOKEN_SECRET);
    }

    @Override
    public String createToken(String subject, Map<String, Object> claimSet) {
        return createToken(HOST,subject,claimSet);
    }

    @Override
    public String decodeToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(HOST).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException exception){
           throw new ErrorResponse(HttpStatus.UNAUTHORIZED,"Unauthorized");
        }
    }

    public String createToken(String host, String subject, Map<String, Object> tokenParam) {
        return JWT.create()
                .withIssuer(host)
                .withSubject(subject)
                .sign(algorithm);
    }
}
