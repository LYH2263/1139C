package com.wordmind.service;

import com.wordmind.dto.MindMapDTO;
import com.wordmind.entity.Word;
import com.wordmind.entity.WordRelation;
import com.wordmind.repository.WordRelationRepository;
import com.wordmind.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MindMapService Unit Tests")
class MindMapServiceTest {

    @Mock
    private WordRepository wordRepository;

    @Mock
    private WordRelationRepository relationRepository;

    @InjectMocks
    private MindMapService mindMapService;

    private Map<Long, Word> wordDatabase;
    private Map<Long, List<WordRelation>> relationDatabase;

    @BeforeEach
    void setUp() {
        wordDatabase = new HashMap<>();
        relationDatabase = new HashMap<>();
    }

    private Word createWord(Long id, String word, String meaning, String pos) {
        Word w = new Word();
        w.setId(id);
        w.setWord(word);
        w.setMeaning(meaning);
        w.setPos(pos);
        return w;
    }

    private WordRelation createRelation(Long id, Long sourceId, Long targetId, WordRelation.RelationType type) {
        WordRelation r = new WordRelation();
        r.setId(id);
        r.setSourceWordId(sourceId);
        r.setTargetWordId(targetId);
        r.setRelationType(type);
        return r;
    }

    private void registerWord(Word word) {
        wordDatabase.put(word.getId(), word);
    }

    private void registerRelation(WordRelation relation) {
        relationDatabase.computeIfAbsent(relation.getSourceWordId(), k -> new ArrayList<>()).add(relation);
        relationDatabase.computeIfAbsent(relation.getTargetWordId(), k -> new ArrayList<>()).add(relation);
    }

    private void setupMockRepository() {
        when(wordRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.ofNullable(wordDatabase.get(id));
        });

        when(relationRepository.findByWordId(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return relationDatabase.getOrDefault(id, Collections.emptyList());
        });
    }

    // =========================================================================
    // Core Functionality Tests
    // =========================================================================

    @Test
    @DisplayName("getMindMap(depth=1) should return correct one-degree nodes and edges")
    void testGetMindMap_Depth1() {
        Word center = createWord(1L, "center", "中心", "noun");
        Word w1 = createWord(2L, "word1", "单词1", "noun");
        Word w2 = createWord(3L, "word2", "单词2", "adj");

        WordRelation r1 = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);
        WordRelation r2 = createRelation(2L, 1L, 3L, WordRelation.RelationType.ANTONYM);

        registerWord(center);
        registerWord(w1);
        registerWord(w2);
        registerRelation(r1);
        registerRelation(r2);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(1L, result.getCenterWord().getId());
        assertEquals(3, result.getNodes().size());

        Set<Long> nodeIds = result.getNodes().stream()
                .map(MindMapDTO.WordNode::getId)
                .collect(Collectors.toSet());
        assertTrue(nodeIds.containsAll(Arrays.asList(1L, 2L, 3L)));

        assertEquals(2, result.getEdges().size());
        Set<String> edgeKeys = result.getEdges().stream()
                .map(e -> e.getSource() + "->" + e.getTarget())
                .collect(Collectors.toSet());
        assertTrue(edgeKeys.contains("1->2"));
        assertTrue(edgeKeys.contains("1->3"));

        verify(wordRepository, times(3)).findById(anyLong());
        verify(relationRepository, times(1)).findByWordId(1L);
    }

    @Test
    @DisplayName("getMindMap(depth=2) should return correct two-degree nodes using BFS")
    void testGetMindMap_Depth2_BFS() {
        Word w1 = createWord(1L, "A", "A", "noun");
        Word w2 = createWord(2L, "B", "B", "noun");
        Word w3 = createWord(3L, "C", "C", "noun");
        Word w4 = createWord(4L, "D", "D", "noun");
        Word w5 = createWord(5L, "E", "E", "noun");

        WordRelation r1 = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);
        WordRelation r2 = createRelation(2L, 1L, 3L, WordRelation.RelationType.ANTONYM);
        WordRelation r3 = createRelation(3L, 2L, 4L, WordRelation.RelationType.TOPIC);
        WordRelation r4 = createRelation(4L, 3L, 5L, WordRelation.RelationType.ROOT);

        registerWord(w1);
        registerWord(w2);
        registerWord(w3);
        registerWord(w4);
        registerWord(w5);
        registerRelation(r1);
        registerRelation(r2);
        registerRelation(r3);
        registerRelation(r4);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 2);

        assertEquals(5, result.getNodes().size());

        Map<Long, Integer> depthMap = result.getNodes().stream()
                .collect(Collectors.toMap(MindMapDTO.WordNode::getId, MindMapDTO.WordNode::getDepth));

        assertEquals(0, depthMap.get(1L));
        assertEquals(1, depthMap.get(2L));
        assertEquals(1, depthMap.get(3L));
        assertEquals(2, depthMap.get(4L));
        assertEquals(2, depthMap.get(5L));

        assertEquals(4, result.getEdges().size());

        verify(relationRepository, times(1)).findByWordId(1L);
        verify(relationRepository, times(1)).findByWordId(2L);
        verify(relationRepository, times(1)).findByWordId(3L);
        verify(relationRepository, never()).findByWordId(4L);
        verify(relationRepository, never()).findByWordId(5L);
    }

    @Test
    @DisplayName("Center node depth=0, depth-1 nodes=1, depth-2 nodes=2")
    void testGetMindMap_DepthVerification() {
        Word center = createWord(1L, "center", "中心", "noun");
        Word d1a = createWord(2L, "d1a", "深度1a", "noun");
        Word d1b = createWord(3L, "d1b", "深度1b", "noun");
        Word d2a = createWord(4L, "d2a", "深度2a", "noun");
        Word d2b = createWord(5L, "d2b", "深度2b", "noun");

        WordRelation r1 = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);
        WordRelation r2 = createRelation(2L, 1L, 3L, WordRelation.RelationType.ANTONYM);
        WordRelation r3 = createRelation(3L, 2L, 4L, WordRelation.RelationType.TOPIC);
        WordRelation r4 = createRelation(4L, 3L, 5L, WordRelation.RelationType.ROOT);

        registerWord(center);
        registerWord(d1a);
        registerWord(d1b);
        registerWord(d2a);
        registerWord(d2b);
        registerRelation(r1);
        registerRelation(r2);
        registerRelation(r3);
        registerRelation(r4);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 2);

        Map<Long, Integer> depthMap = result.getNodes().stream()
                .collect(Collectors.toMap(MindMapDTO.WordNode::getId, MindMapDTO.WordNode::getDepth));

        assertEquals(0, depthMap.get(1L), "Center node should have depth 0");
        assertEquals(1, depthMap.get(2L), "Depth-1 node should have depth 1");
        assertEquals(1, depthMap.get(3L), "Depth-1 node should have depth 1");
        assertEquals(2, depthMap.get(4L), "Depth-2 node should have depth 2");
        assertEquals(2, depthMap.get(5L), "Depth-2 node should have depth 2");
    }

    // =========================================================================
    // Boundary Scenario Tests
    // =========================================================================

    @Test
    @DisplayName("wordId not exists should throw RuntimeException")
    void testGetMindMap_WordNotFound() {
        when(wordRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mindMapService.getMindMap(999L, 2);
        });

        assertEquals("单词不存在", exception.getMessage());
        verify(relationRepository, never()).findByWordId(anyLong());
    }

    @Test
    @DisplayName("Center word with no relations should return only center node")
    void testGetMindMap_NoRelations() {
        Word center = createWord(1L, "isolated", "孤立的", "adj");
        registerWord(center);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 3);

        assertEquals(1, result.getNodes().size());
        assertEquals(1L, result.getNodes().get(0).getId());
        assertEquals(0, result.getNodes().get(0).getDepth());
        assertEquals(0, result.getEdges().size());

        verify(relationRepository, times(1)).findByWordId(1L);
    }

    @Test
    @DisplayName("Cyclic relations (A->B->C->A) should not cause infinite loop")
    void testGetMindMap_CyclicRelations_NoInfiniteLoop() {
        Word a = createWord(1L, "A", "A", "noun");
        Word b = createWord(2L, "B", "B", "noun");
        Word c = createWord(3L, "C", "C", "noun");

        WordRelation ab = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);
        WordRelation bc = createRelation(2L, 2L, 3L, WordRelation.RelationType.SYNONYM);
        WordRelation ca = createRelation(3L, 3L, 1L, WordRelation.RelationType.SYNONYM);

        registerWord(a);
        registerWord(b);
        registerWord(c);
        registerRelation(ab);
        registerRelation(bc);
        registerRelation(ca);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 5);

        assertEquals(3, result.getNodes().size());

        Set<Long> nodeIds = result.getNodes().stream()
                .map(MindMapDTO.WordNode::getId)
                .collect(Collectors.toSet());
        assertTrue(nodeIds.containsAll(Arrays.asList(1L, 2L, 3L)));

        assertEquals(3, result.getEdges().size());

        verify(wordRepository, atMost(3)).findById(anyLong());
        verify(relationRepository, atMost(3)).findByWordId(anyLong());
    }

    @Test
    @DisplayName("depth=0 should return only center node")
    void testGetMindMap_DepthZero() {
        Word center = createWord(1L, "center", "中心", "noun");
        Word w1 = createWord(2L, "word1", "单词1", "noun");
        WordRelation r1 = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);

        registerWord(center);
        registerWord(w1);
        registerRelation(r1);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 0);

        assertEquals(1, result.getNodes().size());
        assertEquals(1L, result.getNodes().get(0).getId());
        assertEquals(0, result.getEdges().size());

        verify(relationRepository, never()).findByWordId(anyLong());
    }

    @Test
    @DisplayName("Large graph with 50 nodes complete graph should handle correctly")
    void testGetMindMap_LargeCompleteGraph_Performance() {
        int nodeCount = 50;
        List<Word> words = new ArrayList<>();
        for (long i = 1; i <= nodeCount; i++) {
            Word w = createWord(i, "word" + i, "单词" + i, "noun");
            words.add(w);
            registerWord(w);
        }

        long relId = 1;
        for (int i = 0; i < words.size(); i++) {
            for (int j = i + 1; j < words.size(); j++) {
                WordRelation rel = createRelation(relId++,
                        words.get(i).getId(),
                        words.get(j).getId(),
                        WordRelation.RelationType.SYNONYM);
                registerRelation(rel);
            }
        }

        setupMockRepository();

        long startTime = System.currentTimeMillis();
        MindMapDTO.Response result = mindMapService.getMindMap(1L, 2);
        long endTime = System.currentTimeMillis();

        assertTrue((endTime - startTime) < 5000, "Should complete within 5 seconds, took: " + (endTime - startTime) + "ms");

        assertEquals(nodeCount, result.getNodes().size(), "All 50 nodes should be returned");

        long depth0Count = result.getNodes().stream().filter(n -> n.getDepth() == 0).count();
        long depth1Count = result.getNodes().stream().filter(n -> n.getDepth() == 1).count();

        assertEquals(1, depth0Count, "Should have 1 center node");
        assertEquals(nodeCount - 1, depth1Count, "All other nodes should be at depth 1");
    }

    // =========================================================================
    // Data Integrity Tests
    // =========================================================================

    @Test
    @DisplayName("Missing related word (findById returns empty) should be skipped without NPE")
    void testGetMindMap_MissingRelatedWord_SkippedNoNPE() {
        Word center = createWord(1L, "center", "中心", "noun");
        WordRelation r1 = createRelation(1L, 1L, 999L, WordRelation.RelationType.SYNONYM);
        WordRelation r2 = createRelation(2L, 1L, 2L, WordRelation.RelationType.ANTONYM);
        Word w2 = createWord(2L, "word2", "单词2", "noun");

        registerWord(center);
        registerWord(w2);
        registerRelation(r1);
        registerRelation(r2);

        when(wordRepository.findById(1L)).thenReturn(Optional.of(center));
        when(wordRepository.findById(2L)).thenReturn(Optional.of(w2));
        when(wordRepository.findById(999L)).thenReturn(Optional.empty());
        when(relationRepository.findByWordId(1L)).thenReturn(Arrays.asList(r1, r2));
        when(relationRepository.findByWordId(2L)).thenReturn(Collections.emptyList());

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(2, result.getNodes().size());
        Set<Long> nodeIds = result.getNodes().stream()
                .map(MindMapDTO.WordNode::getId)
                .collect(Collectors.toSet());
        assertTrue(nodeIds.contains(1L));
        assertTrue(nodeIds.contains(2L));
        assertFalse(nodeIds.contains(999L));

        assertEquals(2, result.getEdges().size());
    }

    @Test
    @DisplayName("Bidirectional relations (A->B and B->A) should not cause duplicate nodes")
    void testGetMindMap_BidirectionalRelations_NoDuplicateNodes() {
        Word a = createWord(1L, "A", "A", "noun");
        Word b = createWord(2L, "B", "B", "noun");

        WordRelation ab = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);
        WordRelation ba = createRelation(2L, 2L, 1L, WordRelation.RelationType.SYNONYM);

        registerWord(a);
        registerWord(b);
        registerRelation(ab);
        registerRelation(ba);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(2, result.getNodes().size(), "Should have exactly 2 unique nodes");

        Set<Long> nodeIds = result.getNodes().stream()
                .map(MindMapDTO.WordNode::getId)
                .collect(Collectors.toSet());
        assertEquals(2, nodeIds.size(), "Node IDs should be unique");
        assertTrue(nodeIds.containsAll(Arrays.asList(1L, 2L)));

        assertEquals(2, result.getEdges().size(), "Both bidirectional edges should be present");
    }

    // =========================================================================
    // Edge Directionality Tests
    // =========================================================================

    @Test
    @DisplayName("Current node as sourceWordId should extract targetWordId correctly")
    void testGetMindMap_CurrentNodeAsSource() {
        Word center = createWord(1L, "center", "中心", "noun");
        Word target = createWord(2L, "target", "目标", "noun");

        WordRelation rel = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);

        registerWord(center);
        registerWord(target);
        registerRelation(rel);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(2, result.getNodes().size());

        Optional<MindMapDTO.WordNode> targetNode = result.getNodes().stream()
                .filter(n -> n.getId().equals(2L))
                .findFirst();
        assertTrue(targetNode.isPresent());
        assertEquals(1, targetNode.get().getDepth());

        assertEquals(1, result.getEdges().size());
        MindMapDTO.RelationEdge edge = result.getEdges().get(0);
        assertEquals(1L, edge.getSource());
        assertEquals(2L, edge.getTarget());
        assertEquals("SYNONYM", edge.getRelationType());
        assertEquals("同义", edge.getLabel());
    }

    @Test
    @DisplayName("Current node as targetWordId should extract sourceWordId correctly")
    void testGetMindMap_CurrentNodeAsTarget() {
        Word center = createWord(1L, "center", "中心", "noun");
        Word source = createWord(2L, "source", "来源", "noun");

        WordRelation rel = createRelation(1L, 2L, 1L, WordRelation.RelationType.ANTONYM);

        registerWord(center);
        registerWord(source);
        registerRelation(rel);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(2, result.getNodes().size());

        Optional<MindMapDTO.WordNode> sourceNode = result.getNodes().stream()
                .filter(n -> n.getId().equals(2L))
                .findFirst();
        assertTrue(sourceNode.isPresent());
        assertEquals(1, sourceNode.get().getDepth());

        assertEquals(1, result.getEdges().size());
        MindMapDTO.RelationEdge edge = result.getEdges().get(0);
        assertEquals(2L, edge.getSource());
        assertEquals(1L, edge.getTarget());
        assertEquals("ANTONYM", edge.getRelationType());
        assertEquals("反义", edge.getLabel());
    }

    @Test
    @DisplayName("Mixed source/target relations should extract relatedWordId correctly for all")
    void testGetMindMap_MixedSourceTargetRelations() {
        Word center = createWord(1L, "center", "中心", "noun");
        Word w2 = createWord(2L, "w2", "单词2", "noun");
        Word w3 = createWord(3L, "w3", "单词3", "noun");
        Word w4 = createWord(4L, "w4", "单词4", "noun");

        WordRelation r1 = createRelation(1L, 1L, 2L, WordRelation.RelationType.SYNONYM);
        WordRelation r2 = createRelation(2L, 3L, 1L, WordRelation.RelationType.ANTONYM);
        WordRelation r3 = createRelation(3L, 1L, 4L, WordRelation.RelationType.TOPIC);

        registerWord(center);
        registerWord(w2);
        registerWord(w3);
        registerWord(w4);
        registerRelation(r1);
        registerRelation(r2);
        registerRelation(r3);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(4, result.getNodes().size());

        Set<Long> nodeIds = result.getNodes().stream()
                .map(MindMapDTO.WordNode::getId)
                .collect(Collectors.toSet());
        assertTrue(nodeIds.containsAll(Arrays.asList(1L, 2L, 3L, 4L)));

        Map<Long, Integer> depthMap = result.getNodes().stream()
                .collect(Collectors.toMap(MindMapDTO.WordNode::getId, MindMapDTO.WordNode::getDepth));
        assertEquals(0, depthMap.get(1L));
        assertEquals(1, depthMap.get(2L));
        assertEquals(1, depthMap.get(3L));
        assertEquals(1, depthMap.get(4L));
    }

    @Test
    @DisplayName("All relation types should map to correct labels")
    void testGetMindMap_AllRelationTypeLabels() {
        Word center = createWord(1L, "center", "中心", "noun");

        WordRelation.RelationType[] types = WordRelation.RelationType.values();
        String[] expectedLabels = {"同义", "反义", "主题", "词根", "前缀", "后缀", "场景"};

        for (int i = 0; i < types.length; i++) {
            Word w = createWord((long) (i + 2), "word" + i, "单词" + i, "noun");
            WordRelation rel = createRelation((long) (i + 1), 1L, w.getId(), types[i]);
            registerWord(w);
            registerRelation(rel);
        }

        registerWord(center);
        setupMockRepository();

        MindMapDTO.Response result = mindMapService.getMindMap(1L, 1);

        assertEquals(types.length + 1, result.getNodes().size());
        assertEquals(types.length, result.getEdges().size());

        Map<String, String> labelMap = result.getEdges().stream()
                .collect(Collectors.toMap(MindMapDTO.RelationEdge::getRelationType, MindMapDTO.RelationEdge::getLabel));

        for (int i = 0; i < types.length; i++) {
            assertEquals(expectedLabels[i], labelMap.get(types[i].name()),
                    "Label for " + types[i] + " should be '" + expectedLabels[i] + "'");
        }
    }
}
