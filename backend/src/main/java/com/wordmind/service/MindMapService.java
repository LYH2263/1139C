package com.wordmind.service;

import com.wordmind.dto.MindMapDTO;
import com.wordmind.entity.Word;
import com.wordmind.entity.WordRelation;
import com.wordmind.repository.WordRelationRepository;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MindMapService {
    
    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private WordRelationRepository relationRepository;
    
    public MindMapDTO.Response getMindMap(Long wordId, int depth) {
        Word centerWord = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("单词不存在"));
        
        Set<Long> visitedWordIds = new HashSet<>();
        List<MindMapDTO.WordNode> nodes = new ArrayList<>();
        List<MindMapDTO.RelationEdge> edges = new ArrayList<>();
        
        // 添加中心节点
        MindMapDTO.WordNode centerNode = createNode(centerWord, 0);
        nodes.add(centerNode);
        visitedWordIds.add(centerWord.getId());
        
        // BFS遍历
        Queue<Word> queue = new LinkedList<>();
        queue.offer(centerWord);
        
        int currentDepth = 0;
        while (!queue.isEmpty() && currentDepth < depth) {
            int levelSize = queue.size();
            Set<Word> nextLevel = new HashSet<>();
            
            for (int i = 0; i < levelSize; i++) {
                Word current = queue.poll();
                List<WordRelation> relations = relationRepository.findByWordId(current.getId());
                
                for (WordRelation relation : relations) {
                    Long relatedWordId = relation.getSourceWordId().equals(current.getId()) 
                        ? relation.getTargetWordId() 
                        : relation.getSourceWordId();
                    
                    if (!visitedWordIds.contains(relatedWordId)) {
                        Word relatedWord = wordRepository.findById(relatedWordId).orElse(null);
                        if (relatedWord != null) {
                            MindMapDTO.WordNode node = createNode(relatedWord, currentDepth + 1);
                            nodes.add(node);
                            visitedWordIds.add(relatedWordId);
                            nextLevel.add(relatedWord);
                        }
                    }
                    
                    // 添加边
                    edges.add(createEdge(relation));
                }
            }
            
            queue.addAll(nextLevel);
            currentDepth++;
        }
        
        return MindMapDTO.Response.builder()
                .centerWord(centerNode)
                .nodes(nodes)
                .edges(edges)
                .build();
    }
    
    private MindMapDTO.WordNode createNode(Word word, int depth) {
        return MindMapDTO.WordNode.builder()
                .id(word.getId())
                .word(word.getWord())
                .meaning(word.getMeaning())
                .category(word.getPos())
                .depth(depth)
                .build();
    }
    
    private MindMapDTO.RelationEdge createEdge(WordRelation relation) {
        String label = getRelationLabel(relation.getRelationType());
        return MindMapDTO.RelationEdge.builder()
                .source(relation.getSourceWordId())
                .target(relation.getTargetWordId())
                .relationType(relation.getRelationType().name())
                .label(label)
                .build();
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
