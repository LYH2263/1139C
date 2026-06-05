package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReviewDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提交复习请求")
    public static class SubmitRequest {
        @NotNull(message = "单词ID不能为空")
        @Schema(description = "单词ID", example = "2001", required = true)
        private Long wordId;

        @NotNull(message = "复习结果不能为空")
        @Schema(description = "复习结果", example = "remember", allowableValues = {"remember", "forgot", "hard", "easy"}, required = true)
        private String result;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "复习记录响应")
    public static class Response {
        @Schema(description = "复习记录ID", example = "5001", required = true)
        private Long id;

        @Schema(description = "单词ID", example = "2001", required = true)
        private Long wordId;

        @Schema(description = "单词", example = "ubiquitous", required = true)
        private String word;

        @Schema(description = "单词释义", example = "无处不在的，普遍存在的", required = true)
        private String meaning;

        @Schema(description = "复习结果", example = "remember", allowableValues = {"remember", "forgot", "hard", "easy"}, required = true)
        private String result;

        @Schema(description = "熟练程度(0-100)", example = "85", required = true)
        private Integer proficiency;

        @Schema(description = "下次复习时间", example = "2024-01-17T10:30:00", required = true)
        private LocalDateTime nextReviewAt;

        @Schema(description = "创建时间", example = "2024-01-15T10:30:00", required = true)
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "今日复习列表响应")
    public static class TodayResponse {
        @Schema(description = "今日复习记录列表", required = true)
        private java.util.List<Response> list;

        @Schema(description = "今日复习总数", example = "25", required = true)
        private Long total;
    }
}
