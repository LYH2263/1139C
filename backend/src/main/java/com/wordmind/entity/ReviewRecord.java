package com.wordmind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_records")
@Data
public class ReviewRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "word_id", nullable = false)
    private Long wordId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewResult result;
    
    @Column(nullable = false)
    private Integer proficiency = 0;
    
    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal ef = new BigDecimal("2.5");
    
    @Column(nullable = false)
    private Integer repetitions = 0;
    
    @Column(name = "interval_days")
    private Integer intervalDays = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum ReviewResult {
        KNOWN, VAGUE, UNKNOWN
    }
}
