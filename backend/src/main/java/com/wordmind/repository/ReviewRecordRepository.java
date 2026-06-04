package com.wordmind.repository;

import com.wordmind.entity.ReviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRecordRepository extends JpaRepository<ReviewRecord, Long> {
    
    @Query("SELECT rr FROM ReviewRecord rr WHERE rr.userId = :userId AND " +
           "(rr.nextReviewAt IS NULL OR rr.nextReviewAt <= :now) " +
           "ORDER BY rr.nextReviewAt ASC")
    List<ReviewRecord> findTodayReviews(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    Optional<ReviewRecord> findByUserIdAndWordId(Long userId, Long wordId);
    
    @Query("SELECT COUNT(rr) FROM ReviewRecord rr WHERE rr.userId = :userId AND rr.result = 'KNOWN'")
    Long countKnownWordsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(rr) FROM ReviewRecord rr WHERE rr.userId = :userId AND " +
           "rr.createdAt >= :startOfDay AND rr.createdAt < :endOfDay")
    Long countTodayReviewsByUserId(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
