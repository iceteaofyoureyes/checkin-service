package com.wiinvent.checkinservice.repository;

import com.wiinvent.checkinservice.entity.WalletTransaction;
import com.wiinvent.checkinservice.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Page<WalletTransaction> findByWalletUserUserIdAndTxnType(Long userId, TransactionType transactionType, Pageable pageable);
    Page<WalletTransaction> findByWalletUserUserId(Long userId, Pageable pageable);
}
