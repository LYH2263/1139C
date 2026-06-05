package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StatsDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "学习统计响应")
    public static class Response {
        @Schema(description = "学习单词总数", example = "1568", required = true)
        private Long totalWords;

        @Schema(description = "今日复习数量", example = "42", required = true)
        private Long todayReviewCount;

        @Schema(description = "答题准确率(0-1)", example = "0.92", required = true)
        private Double accuracy;

        @Schema(description = "连续学习天数", example = "15", required = true)
        private Integer streakDays;
    }
}
