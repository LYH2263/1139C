package com.wordmind.dto;

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
    public static class CreateRequest {
        @NotNull(message = "单词ID不能为空")
        private Long wordId;
        
        @NotNull(message = "计划类型不能为空")
        private String planType;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long wordId;
        private String word;
        private String meaning;
        private String planType;
        private String createdAt;
    }
}
