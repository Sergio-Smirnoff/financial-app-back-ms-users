package com.financialapp.users.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences", schema = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesJpaEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "max_idle_minutes", nullable = false)
    private int maxIdleMinutes;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Column(name = "primary_currency", nullable = false, length = 3)
    private String primaryCurrency;

    @Column(name = "secondary_currency", length = 10)
    private String secondaryCurrency;

    @Column(name = "number_format", nullable = false, length = 10)
    private String numberFormat;

    @Column(nullable = false)
    private int decimals;

    @Column(name = "color_for_amounts", nullable = false)
    private boolean colorForAmounts;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
    }
}
