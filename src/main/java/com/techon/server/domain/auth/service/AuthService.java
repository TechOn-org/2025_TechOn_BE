package com.techon.server.domain.auth.service;

import com.techon.server.domain.auth.dto.AuthDtos;
import com.techon.server.domain.auth.entity.UserAccount;
import com.techon.server.domain.auth.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public AuthDtos.AuthResponse signup(AuthDtos.SignupRequest req){
        if (req.userId()==null || req.userId().isBlank() ||
                req.nickname()==null || req.nickname().isBlank() ||
                req.password()==null || req.password().isBlank()) {
            return AuthDtos.AuthResponse.fail("필수값 누락");
        }
        if (repo.existsByUsername(req.userId())) {
            return AuthDtos.AuthResponse.fail("이미 사용 중인 아이디");
        }
        UserAccount u = UserAccount.builder()
                .username(req.userId().trim())
                .nickname(req.nickname().trim())
                .passwordHash(encoder.encode(req.password()))
                .build();
        repo.save(u);
        return AuthDtos.AuthResponse.ok(u.getId(), u.getNickname());
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req){
        var u = repo.findByUsername(req.username()).orElse(null);
        if (u==null) return AuthDtos.AuthResponse.fail("아이디 또는 비밀번호 불일치");
        if (!encoder.matches(req.password(), u.getPasswordHash()))
            return AuthDtos.AuthResponse.fail("아이디 또는 비밀번호 불일치");
        return AuthDtos.AuthResponse.ok(u.getId(), u.getNickname());
    }
}
