package ru.kpfu.synergy.authserver.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
