package com.wordmind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_records")
@Data
public class QuizRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Integer score;
    
    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;
    
    @Column(name = "total_count", nullable = false)
    private Integer totalCount;
    
    @Column(nullable = false)
    private Integer duration;
    
    @Column(name = "wrong_word_ids", length = 500)
    private String wrongWordIds;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
