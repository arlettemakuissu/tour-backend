package com.odissey.auth_service.repository;

import com.odissey.auth_service.entity.RefreshToken;
import org.hibernate.validator.constraints.Mod10Check;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRefreshRepository extends JpaRepository<RefreshToken, String> {

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rf " +
            "SET rf.revoked = true " +
            "WHERE rf.userId = :userId " +
            "AND rf.revoked = false")
    void revokeOldRefreshTokensByUser(int userId);
}
