package ru.kpfu.synergy.authserver.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.synergy.authserver.dto.AuthDto;
import ru.kpfu.synergy.authserver.exception.AuthException;
import ru.kpfu.synergy.authserver.exception.UserExistException;
import ru.kpfu.synergy.authserver.model.RefreshToken;
import ru.kpfu.synergy.authserver.model.User;
import ru.kpfu.synergy.authserver.repository.RefreshTokenRepository;
import ru.kpfu.synergy.authserver.repository.UserRepository;
import ru.kpfu.synergy.authserver.service.api.AuthService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("secret")
    private String secret;
    private static final String LOGIN_CLAIM = "login";

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthDto auth(String login, String password) throws AuthException {
        Optional<User> userOptional = userRepository.findByLogin(login);
        User user = userOptional.orElseThrow(AuthException::new);
        if (!passwordEncoder.matches(password, user.getHashPassword())) {
            throw new AuthException();
        }
        return AuthDto.builder()
                .accessToken(generateAccessToken(user.getLogin()))
                .refreshToken(generateRefreshToken(user))
                .build();
    }

    @Transactional
    @Override
    public AuthDto refresh(String refreshToken) throws AuthException {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.getByToken(refreshToken);
        RefreshToken refreshTokenObject = refreshTokenOptional.orElseThrow(AuthException::new);
        String login = refreshTokenObject.getUser().getLogin();
        refreshTokenRepository.delete(refreshTokenObject);
        return AuthDto.builder()
                .accessToken(generateAccessToken(login))
                .refreshToken(generateRefreshToken(refreshTokenObject.getUser()))
                .build();
    }

    @Override
    @Transactional
    public AuthDto registration(String login, String password) throws AuthException {
        if (userRepository.existsByLogin(login)) {
            throw new UserExistException();
        }
        User user = User.builder()
                .login(login)
                .hashPassword(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);
        return AuthDto.builder()
                .accessToken(generateAccessToken(user.getLogin()))
                .refreshToken(generateRefreshToken(user))
                .build();
    }

    private String generateAccessToken(String login) throws AuthException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                    .withIssuer("authServer")
                    .withClaim(LOGIN_CLAIM, login)
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new AuthException();
        }
    }

    private String generateRefreshToken(User user) throws AuthException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withExpiresAt(Date.from(Instant.now().plus(60, ChronoUnit.DAYS)))
                    .withIssuer("authServer")
                    .withClaim(LOGIN_CLAIM, user.getLogin())
                    .sign(algorithm);
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(token)
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
            return token;
        } catch (JWTCreationException exception) {
            throw new AuthException();
        }
    }
}
