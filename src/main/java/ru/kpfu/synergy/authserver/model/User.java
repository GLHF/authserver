package ru.kpfu.synergy.authserver.model;


import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_user_id_seq")
    @SequenceGenerator(name = "chat_user_id_seq", allocationSize = 1)
    private Long id;
    private String login;
    @Column(name = "hash_password")
    private String hashPassword;
}
