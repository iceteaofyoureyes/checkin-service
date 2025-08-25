package com.wiinvent.checkinservice.entity;

import com.wiinvent.checkinservice.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "wallet_txn",
        indexes = {
                @Index(name = "idx_txn_wallet_created", columnList = "wallet_id, created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_wallet_txn_idem", columnNames = {"ref_code"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false, foreignKey = @ForeignKey(name = "fk_txn_wallet"))
    private Wallet wallet;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "txn_type", length = 32, nullable = false)
    private TransactionType txnType;

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "txn_ref_code", length = 128, nullable = false, unique = true)
    private String refCode;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
}
