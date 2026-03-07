package com.sphere.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String code;
    private LocalDateTime expiresAt;
    private boolean used;

    @PrePersist
    public void prePersist() {
        expiresAt = LocalDateTime.now().plusMinutes(10);
    }
}