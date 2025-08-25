package com.wiinvent.checkinservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
}
