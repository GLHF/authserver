package ru.kpfu.synergy.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.synergy.authserver.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> getByToken(String token);
}
