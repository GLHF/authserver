package ru.kpfu.synergy.authserver.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthForm {
    private String login;
    private String password;
}
