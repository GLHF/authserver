package ru.kpfu.synergy.authserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth")
    public ResponseEntity auth(@RequestBody AuthForm authForm) {
        try {
            AuthDto authDto = authService.auth(authForm.getLogin(), authForm.getPassword());
            return ResponseEntity.ok(authDto);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Auth is failure");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity auth(@RequestBody RefreshForm refreshToken) {
        try {
            AuthDto authDto = authService.refresh(refreshToken.getToken());
            return ResponseEntity.ok(authDto);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh is failure");
        }
    }

    @PostMapping("/registration")
    public ResponseEntity registration(@RequestBody RegistrationForm registrationForm) {
        try {
            AuthDto authDto = authService.registration(registrationForm.getLogin(), registrationForm.getPassword());
            return ResponseEntity.ok(authDto);
        }catch (UserExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with login already exist");
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration is failure");
        }
    }
}
