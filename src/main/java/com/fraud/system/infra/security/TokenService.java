package com.fraud.system.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fraud.system.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final StringRedisTemplate redisTemplate;

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

            // Alterar depois para salvar o token no Redis com expiração em 10 minutos (600 segundos)
            saveTokenInRedis(user.getLogin(), token, 1 * 60);

            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    // Alterar depois para o token expirar em 10 minutos (600 segundos)
    private Instant genExpiration() {
        return Instant.now().plus(60, ChronoUnit.SECONDS);
    }

    private void saveTokenInRedis(String username, String token, long expirationSeconds) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(username, token, expirationSeconds, TimeUnit.SECONDS);
    }
}
