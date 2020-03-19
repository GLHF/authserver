package ru.kpfu.synergy.authserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.synergy.authserver.dto.AuthDto;
import ru.kpfu.synergy.authserver.exception.AuthException;
import ru.kpfu.synergy.authserver.exception.UserExistException;
import ru.kpfu.synergy.authserver.service.api.AuthService;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity auth(@RequestParam String login, @RequestParam String password) {
        try {
            AuthDto authDto = authService.auth(login, password);
            return ResponseEntity.ok(authDto);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Auth is failure");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity auth(@RequestParam String refreshToken) {
        try {
            AuthDto authDto = authService.refresh(refreshToken);
            return ResponseEntity.ok(authDto);
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh is failure");
        }
    }

    @PostMapping("/registration")
    public ResponseEntity registration(@RequestParam String login, @RequestParam String password) {
        try {
            AuthDto authDto = authService.registration(login, password);
            return ResponseEntity.ok(authDto);
        }catch (UserExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with login already exist");
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration is failure");
        }
    }
}
