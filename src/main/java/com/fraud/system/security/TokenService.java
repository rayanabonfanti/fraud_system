package com.fraud.system.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.system.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private final StringRedisTemplate redisTemplate;
    @Value("${api.security.token.secret}")
    private String secret;

    public TokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getLogin())
                    .withExpiresAt(genExpiration())
                    .sign(algorithm);

            saveUserInRedis(user, token, 10 * 60);

            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public User validateTokenAndRetrieveUser(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

            if (subject != null) {
                String userJson = getUserFromRedis(subject);
                String tokenRedis = getUsernameFromRedis(subject).trim();
                if (userJson != null && userJson.trim().equalsIgnoreCase(tokenRedis)) {
                    return convertJsonToUser(userJson);
                }
            }
            return null;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public Instant genExpiration() {
        return Instant.now().plus(600, ChronoUnit.SECONDS);
    }

    private String getUsernameFromRedis(String subject) {
        return redisTemplate.opsForValue().get(subject);
    }

    private String getUserFromRedis(String subject) {
        return redisTemplate.opsForValue().get(subject);
    }

    private void saveUserInRedis(User user, String token, long expirationSeconds) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String userJson = convertUserToJson(user);
        ops.set(user.getLogin(), userJson, expirationSeconds, TimeUnit.SECONDS);
    }

    private User convertJsonToUser(String userJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(userJson, User.class);
            return user;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to User", e);
        }
    }

    private String convertUserToJson(User user) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String returnJson = objectMapper.writeValueAsString(user);
            return returnJson;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting User to JSON", e);
        }
    }

}
