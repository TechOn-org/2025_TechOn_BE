package com.techon.server.domain.auth.repository;

import com.techon.server.domain.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    boolean existsByUsername(String username);
    Optional<UserAccount> findByUsername(String username);
}
