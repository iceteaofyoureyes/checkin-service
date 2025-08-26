package com.wiinvent.checkinservice.repository;

import com.wiinvent.checkinservice.entity.CheckinConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckinConfigRepository extends JpaRepository<CheckinConfig, Long> {
    Optional<CheckinConfig> findFirstByIsActiveTrue();
}
