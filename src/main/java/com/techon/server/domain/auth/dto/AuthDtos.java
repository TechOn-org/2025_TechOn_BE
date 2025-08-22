package com.techon.server.domain.auth.dto;

public class AuthDtos {
    public record SignupRequest(String username, String nickname, String password) {}
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(boolean success, String message, Long userId, String nickname) {
        public static AuthResponse ok(Long id, String nick){ return new AuthResponse(true, "OK", id, nick); }
        public static AuthResponse fail(String msg){ return new AuthResponse(false, msg, null, null); }
    }
}
