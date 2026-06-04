package com.wordmind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class WordDTO {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "单词不能为空")
        @Size(max = 100, message = "单词长度不能超过100字符")
        private String word;
        
        @Size(max = 100, message = "音标长度不能超过100字符")
        private String phonetic;
        
        @Size(max = 50, message = "词性长度不能超过50字符")
        private String pos;
        
        @NotBlank(message = "释义不能为空")
        @Size(max = 500, message = "释义长度不能超过500字符")
        private String meaning;
        
        @Size(max = 1000, message = "例句长度不能超过1000字符")
        private String example;
        
        @Size(max = 500, message = "记忆提示长度不能超过500字符")
        private String memoryTip;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @Size(max = 100, message = "单词长度不能超过100字符")
        private String word;
        
        @Size(max = 100, message = "音标长度不能超过100字符")
        private String phonetic;
        
        @Size(max = 50, message = "词性长度不能超过50字符")
        private String pos;
        
        @Size(max = 500, message = "释义长度不能超过500字符")
        private String meaning;
        
        @Size(max = 1000, message = "例句长度不能超过1000字符")
        private String example;
        
        @Size(max = 500, message = "记忆提示长度不能超过500字符")
        private String memoryTip;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String word;
        private String phonetic;
        private String pos;
        private String meaning;
        private String example;
        private String memoryTip;
        private LocalDateTime createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private java.util.List<Response> list;
        private Long total;
        private Integer page;
        private Integer size;
    }
}
