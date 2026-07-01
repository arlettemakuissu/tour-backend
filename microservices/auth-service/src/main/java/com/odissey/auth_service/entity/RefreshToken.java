package com.odissey.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens",
        indexes = {@Index(name = "IDX_refresh_tokens_user_id", columnList = "userId")}
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshToken {

    @Id
    @EqualsAndHashCode.Include
    @Column(length = 36)
    private String id; // UUID

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false, length = 50)
    private String ip;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

}
