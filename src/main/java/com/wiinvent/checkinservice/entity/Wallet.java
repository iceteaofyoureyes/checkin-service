package com.wiinvent.checkinservice.entity;

import com.wiinvent.checkinservice.entity.enums.WalletType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "wallets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_wallet_user_type", columnNames = {"user_id", "wallet_type"}),
                @UniqueConstraint(name = "wallets_wallet_code_key", columnNames = {"wallet_code"})
        },
        indexes = {
                @Index(name = "idx_wallet_user", columnList = "user_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long id;

    @Column(name = "wallet_code", nullable = false, length = 255)
    private String walletCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_wallet_user")
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 32)
    private WalletType walletType;

    @Column(name = "balance", nullable = false)
    private long balance;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
