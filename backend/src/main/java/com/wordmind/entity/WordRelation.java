package com.wordmind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "word_relations")
@Data
public class WordRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source_word_id", nullable = false)
    private Long sourceWordId;
    
    @Column(name = "target_word_id", nullable = false)
    private Long targetWordId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false, length = 20)
    private RelationType relationType;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum RelationType {
        SYNONYM,    // 同义词
        ANTONYM,    // 反义词
        TOPIC,      // 主题相关
        ROOT,       // 词根
        PREFIX,     // 前缀
        SUFFIX,     // 后缀
        SCENE       // 场景相关
    }
}
