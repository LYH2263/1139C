package com.wordmind.service;

import com.wordmind.dto.MindMapDTO;
import com.wordmind.entity.Word;
import com.wordmind.entity.WordRelation;
import com.wordmind.repository.WordRelationRepository;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelationService {
    
    @Autowired
    private WordRelationRepository relationRepository;
    
    @Autowired
    private WordRepository wordRepository;
    
    @Transactional
    public MindMapDTO.RelationEdge createRelation(MindMapDTO.RelationRequest request) {
        // 验证源单词存在
        Word sourceWord = wordRepository.findById(request.getSourceWordId())
                .orElseThrow(() -> new RuntimeException("源单词不存在"));
        
        // 验证目标单词存在
        Word targetWord = wordRepository.findById(request.getTargetWordId())
                .orElseThrow(() -> new RuntimeException("目标单词不存在"));
        
        // 验证关系类型有效
        WordRelation.RelationType relationType;
        try {
            relationType = WordRelation.RelationType.valueOf(request.getRelationType());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的关系类型");
        }
        
        // 检查关系是否已存在
        boolean exists = relationRepository.existsBySourceWordIdAndTargetWordIdAndRelationType(
                request.getSourceWordId(), request.getTargetWordId(), relationType);
        if (exists) {
            throw new RuntimeException("该关系已存在");
        }
        
        // 创建关系
        WordRelation relation = new WordRelation();
        relation.setSourceWordId(request.getSourceWordId());
        relation.setTargetWordId(request.getTargetWordId());
        relation.setRelationType(relationType);
        
        WordRelation saved = relationRepository.save(relation);
        
        return MindMapDTO.RelationEdge.builder()
                .source(saved.getSourceWordId())
                .target(saved.getTargetWordId())
                .relationType(saved.getRelationType().name())
                .label(getRelationLabel(saved.getRelationType()))
                .build();
    }
    
    @Transactional
    public void deleteRelation(Long id) {
        WordRelation relation = relationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("关系不存在"));
        relationRepository.delete(relation);
    }
    
    private String getRelationLabel(WordRelation.RelationType type) {
        switch (type) {
            case SYNONYM: return "同义";
            case ANTONYM: return "反义";
            case TOPIC: return "主题";
            case ROOT: return "词根";
            case PREFIX: return "前缀";
            case SUFFIX: return "后缀";
            case SCENE: return "场景";
            default: return type.name();
        }
    }
}
