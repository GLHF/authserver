package ru.kpfu.synergy.authserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.synergy.authserver.dto.AuthDto;
import ru.kpfu.synergy.authserver.exception.AuthException;
import ru.kpfu.synergy.authserver.exception.UserExistException;
import ru.kpfu.synergy.authserver.form.AuthForm;
import ru.kpfu.synergy.authserver.form.RefreshForm;
import ru.kpfu.synergy.authserver.form.RegistrationForm;
import ru.kpfu.synergy.authserver.service.api.AuthService;

@Slf4j
@RestController
@RequestMapping("/user")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Авторизация с выдачей auth и refresh токенов
     * */
    @PostMapping("/auth")
    public ResponseEntity auth(@RequestBody AuthForm authForm) {
        try {
            log.info("Authorization starts: " + authForm.getLogin());
            AuthDto authDto = authService.auth(authForm.getLogin(), authForm.getPassword());
            log.info("Authorization end: " + authForm.getLogin());
            return ResponseEntity.ok(authDto);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Auth is failure");
        }
    }

    /**
     * Обновление токена по refresh токену
     * */
    @PostMapping("/refresh")
    public ResponseEntity refreshToken(@RequestBody RefreshForm refreshToken) {
        try {
            log.info("Refresh starts: " + refreshToken.getToken());
            AuthDto authDto = authService.refresh(refreshToken.getToken());
            log.info("Refresh end: " + refreshToken.getToken());
            return ResponseEntity.ok(authDto);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh is failure");
        }
    }

    /**
     * Регистрация пользователя
     * */
    @PostMapping("/registration")
    public ResponseEntity registration(@RequestBody RegistrationForm registrationForm) {
        try {
            log.info("Registration starts: " + registrationForm.getLogin());
            AuthDto authDto = authService.registration(registrationForm.getLogin(), registrationForm.getPassword());
            log.info("Registration starts: " + registrationForm.getLogin());
            return ResponseEntity.ok(authDto);
        } catch (UserExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with login already exist");
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration is failure");
        }
    }
}
