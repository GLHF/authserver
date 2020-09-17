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
import ru.kpfu.synergy.authserver.dto.AuthDto;
import ru.kpfu.synergy.authserver.exception.AuthException;
import ru.kpfu.synergy.authserver.exception.UserExistException;
import ru.kpfu.synergy.authserver.model.RefreshToken;
import ru.kpfu.synergy.authserver.model.User;
import ru.kpfu.synergy.authserver.repository.RefreshTokenRepository;
import ru.kpfu.synergy.authserver.repository.UserRepository;
import ru.kpfu.synergy.authserver.service.api.AuthService;
import ru.kpfu.synergy.authserver.service.api.TokenService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("secret")
    private String secret;
    @Value("#{new Integer('${refresh_expire_days}')}")
    private int refreshExpirePeriodDays;
    @Value("#{new Integer('${access_expire_minutes}')}")
    private int accessExpirePeriodMinutes;
    @Value("spring.application.name")
    private String serviceName = "authServer";


    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto auth(String login, String password) throws AuthException {
        Optional<User> userOptional = userRepository.findByLogin(login);
        User user = userOptional.orElseThrow(AuthException::new);
        if (!passwordEncoder.matches(password, user.getHashPassword())) {
            throw new AuthException("Passwords not matches");
        }
        return AuthDto.builder()
                .accessToken(tokenService.generateAccessToken(user.getLogin()))
                .refreshToken(tokenService.generateRefreshToken(user))
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto refresh(String refreshToken) throws AuthException {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.getByToken(refreshToken);
        RefreshToken refreshTokenObject = refreshTokenOptional.orElseThrow(AuthException::new);
        String login = refreshTokenObject.getUser().getLogin();
        refreshTokenRepository.delete(refreshTokenObject);
        return AuthDto.builder()
                .accessToken(tokenService.generateAccessToken(login))
                .refreshToken(tokenService.generateRefreshToken(refreshTokenObject.getUser()))
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthDto registration(String login, String password) throws AuthException {
        if (userRepository.existsByLogin(login)) {
            throw new UserExistException(String.format("User with login %s already exist", login));
        }
        User user = User.builder()
                .login(login)
                .hashPassword(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);
        return AuthDto.builder()
                .accessToken(tokenService.generateAccessToken(user.getLogin()))
                .refreshToken(tokenService.generateRefreshToken(user))
                .build();
    }
}
