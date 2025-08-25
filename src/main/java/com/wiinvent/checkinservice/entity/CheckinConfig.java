package com.wiinvent.checkinservice.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.wiinvent.checkinservice.util.JsonUtils;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "checkin_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckinConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "monthly_limit_days", nullable = false)
    private int monthlyLimitDays;

    // JSONB, store point mapping {"pointConfigs": [1, 2, 3, 5, 8, 13, 21]}
    @Column(name = "config", columnDefinition = "jsonb", nullable = false)
    private String config;

    // JSONB, store time window list [{start:"09:00",end:"11:00"}, {start:"19:00",end:"21:00"}]
    @Column(name = "time_windows", columnDefinition = "jsonb", nullable = false)
    private String timeWindows;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateMonthlyLimit() {
        try {
            JsonNode root = JsonUtils.readTree(config);

            if (Objects.isNull(root) || root.isEmpty()) {
                throw new IllegalStateException("Invalid config");
            }

            JsonNode pointConfig = root.get("pointConfigs");

            if (!pointConfig.isArray() || pointConfig.isEmpty()) {
                throw new IllegalStateException("Invalid pointConfigs");
            }

            int pointsLength = root.get("pointConfigs").size();

            if (monthlyLimitDays != pointsLength) {
                throw new IllegalStateException(
                        "monthlyLimitDays must equal the length of pointConfigs");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid JSON in pointConfig", e);
        }
    }
}
