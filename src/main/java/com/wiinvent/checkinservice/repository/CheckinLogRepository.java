package com.wiinvent.checkinservice.repository;

import com.wiinvent.checkinservice.entity.CheckinLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CheckinLogRepository extends JpaRepository<CheckinLog, Long> {

    boolean existsByUser_UserIdAndCheckinDate(Long userId, LocalDate checkinDate);

    @Query("SELECT COUNT(c) " +
            "FROM CheckinLog c " +
            "WHERE c.user.userId = :userId AND c.checkinDate >= :start AND c.checkinDate < :end")
    int countByUserIdAndCheckinDateBetween(@Param("userId") Long userId,
                                           @Param("start") LocalDate startInclusive,
                                           @Param("end") LocalDate endExclusive);

    @Query("select c from CheckinLog c where c.user.id = :userId and c.checkinDate between :start and :end")
    List<CheckinLog> findByUserAndDateRange(@Param("userId") Long userId,
                                            @Param("start") LocalDate start,
                                            @Param("end") LocalDate end);
}
