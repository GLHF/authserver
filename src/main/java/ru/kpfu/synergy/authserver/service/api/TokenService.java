package ru.kpfu.synergy.authserver.service.api;

import ru.kpfu.synergy.authserver.exception.AuthException;
import ru.kpfu.synergy.authserver.model.User;

public interface TokenService {
    String generateRefreshToken(User user) throws AuthException;
    String generateAccessToken(String login) throws AuthException;
}
