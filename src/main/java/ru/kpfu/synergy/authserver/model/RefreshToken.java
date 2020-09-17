package ru.kpfu.synergy.authserver.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_id_seq")
    @SequenceGenerator(name = "refresh_token_id_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
