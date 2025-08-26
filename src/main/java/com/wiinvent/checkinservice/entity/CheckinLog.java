package com.wiinvent.checkinservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "checkin_log",
        indexes = {
                @Index(name = "idx_checkin_user_month", columnList = "user_id, (checkin_time::date)")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_checkin_user"))
    private User user;

    @Column(name = "checkin_time", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime checkinTime;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.checkinTime = OffsetDateTime.now(ZoneOffset.UTC);
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
