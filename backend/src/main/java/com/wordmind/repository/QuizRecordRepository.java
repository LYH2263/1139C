package com.wordmind.repository;

import com.wordmind.entity.QuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {
    
    List<QuizRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT COUNT(qr) FROM QuizRecord qr WHERE qr.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(qr.score) FROM QuizRecord qr WHERE qr.userId = :userId")
    Double calculateAverageScoreByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(qr) FROM QuizRecord qr WHERE qr.userId = :userId AND " +
           "qr.createdAt >= :startOfDay AND qr.createdAt < :endOfDay")
    Long countTodayQuizzesByUserId(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
