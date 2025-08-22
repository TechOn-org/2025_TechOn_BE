package com.techon.server.domain.auth.controller;

import com.techon.server.domain.auth.dto.AuthDtos;
import com.techon.server.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthDtos.AuthResponse> signup(@RequestBody AuthDtos.SignupRequest request) {
        var res = authService.signup(request);
        return ResponseEntity.status(res.success()?201:400).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@RequestBody AuthDtos.LoginRequest request) {
        var res = authService.login(request);
        return ResponseEntity.status(res.success()?201:400).body(res);
    }
}
