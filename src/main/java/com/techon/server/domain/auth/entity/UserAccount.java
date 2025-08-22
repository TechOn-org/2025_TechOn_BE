package com.techon.server.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_account", uniqueConstraints = @UniqueConstraint(name="uk_user_username", columnNames="userId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=50)
    private String username;     // 아이디 (고유)

    @Column(nullable=false, length=50)
    private String nickname;     // 닉네임

    @Column(nullable=false, length=100)
    private String passwordHash; // BCrypt 해시
}
