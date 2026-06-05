package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class StudyPlanDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "创建学习计划请求")
    public static class CreateRequest {
        @NotNull(message = "单词ID不能为空")
        @Schema(description = "单词ID", example = "4001", required = true)
        private Long wordId;

        @NotNull(message = "计划类型不能为空")
        @Schema(description = "计划类型", example = "spaced_repetition", allowableValues = {"spaced_repetition", "intensive", "review_only", "new_words"}, required = true)
        private String planType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "学习计划响应")
    public static class Response {
        @Schema(description = "学习计划ID", example = "6001", required = true)
        private Long id;

        @Schema(description = "单词ID", example = "4001", required = true)
        private Long wordId;

        @Schema(description = "单词", example = "ephemeral", required = true)
        private String word;

        @Schema(description = "单词释义", example = "短暂的，瞬息的", required = true)
        private String meaning;

        @Schema(description = "计划类型", example = "spaced_repetition", allowableValues = {"spaced_repetition", "intensive", "review_only", "new_words"}, required = true)
        private String planType;

        @Schema(description = "创建时间", example = "2024-01-15T10:30:00", required = true)
        private String createdAt;
    }
}
