package ru.kpfu.synergy.authserver.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.synergy.authserver.exception.AuthException;
import ru.kpfu.synergy.authserver.model.RefreshToken;
import ru.kpfu.synergy.authserver.model.User;
import ru.kpfu.synergy.authserver.repository.RefreshTokenRepository;
import ru.kpfu.synergy.authserver.repository.UserRepository;
import ru.kpfu.synergy.authserver.service.api.TokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("secret")
    private String secret;
    @Value("#{new Integer('${refresh_expire_days}')}")
    private int refreshExpirePeriodDays;
    @Value("#{new Integer('${access_expire_minutes}')}")
    private int accessExpirePeriodMinutes;
    @Value("spring.application.name")
    private String serviceName = "authServer";
    private static final String LOGIN_CLAIM = "login";

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String generateAccessToken(String login) throws AuthException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withExpiresAt(Date.from(Instant.now().plus(accessExpirePeriodMinutes, ChronoUnit.MINUTES)))
                    .withIssuer(serviceName)
                    .withClaim(LOGIN_CLAIM, login)
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new AuthException(exception);
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String generateRefreshToken(User user) throws AuthException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withExpiresAt(Date.from(Instant.now().plus(refreshExpirePeriodDays, ChronoUnit.DAYS)))
                    .withIssuer(serviceName)
                    .withClaim(LOGIN_CLAIM, user.getLogin())
                    .sign(algorithm);
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(token)
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
            return token;
        } catch (JWTCreationException exception) {
            throw new AuthException(exception);
        }
    }
}
