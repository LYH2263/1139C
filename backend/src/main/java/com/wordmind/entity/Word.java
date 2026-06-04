package com.wordmind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "words")
@Data
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String word;
    
    @Column(length = 100)
    private String phonetic;
    
    @Column(length = 50)
    private String pos;
    
    @Column(nullable = false, length = 500)
    private String meaning;
    
    @Column(length = 1000)
    private String example;
    
    @Column(name = "memory_tip", length = 500)
    private String memoryTip;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
