package ru.kpfu.synergy.authserver.service.api;

import ru.kpfu.synergy.authserver.dto.AuthDto;
import ru.kpfu.synergy.authserver.exception.AuthException;


public interface AuthService {
    AuthDto auth(String login, String password) throws AuthException;
    AuthDto refresh(String refreshToken) throws AuthException;
    AuthDto registration(String login, String password) throws AuthException;
}
