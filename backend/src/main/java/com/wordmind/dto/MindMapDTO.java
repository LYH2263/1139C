package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

public class MindMapDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "思维导图响应")
    public static class Response {
        @Schema(description = "中心单词节点", required = true)
        private WordNode centerWord;

        @Schema(description = "所有单词节点列表", required = true)
        private List<WordNode> nodes;

        @Schema(description = "单词关系边列表", required = true)
        private List<RelationEdge> edges;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单词节点")
    public static class WordNode {
        @Schema(description = "单词ID", example = "1001", required = true)
        private Long id;

        @Schema(description = "单词", example = "ephemeral", required = true)
        private String word;

        @Schema(description = "单词释义", example = "短暂的，瞬息的", required = true)
        private String meaning;

        @Schema(description = "单词分类", example = "adjective", required = true)
        private String category;

        @Schema(description = "在思维导图中的深度", example = "1", required = true)
        private Integer depth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单词关系边")
    public static class RelationEdge {
        @Schema(description = "源单词节点ID", example = "1001", required = true)
        private Long source;

        @Schema(description = "目标单词节点ID", example = "1002", required = true)
        private Long target;

        @Schema(description = "关系类型", example = "synonym", allowableValues = {"synonym", "antonym", "derivative", "collocation", "hyponym", "hypernym"}, required = true)
        private String relationType;

        @Schema(description = "关系标签", example = "同义词", required = true)
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "创建单词关系请求")
    public static class RelationRequest {
        @NotNull(message = "源单词ID不能为空")
        @Schema(description = "源单词ID", example = "1001", required = true)
        private Long sourceWordId;

        @NotNull(message = "目标单词ID不能为空")
        @Schema(description = "目标单词ID", example = "1002", required = true)
        private Long targetWordId;

        @NotNull(message = "关系类型不能为空")
        @Schema(description = "关系类型", example = "synonym", allowableValues = {"synonym", "antonym", "derivative", "collocation", "hyponym", "hypernym"}, required = true)
        private String relationType;
    }
}
