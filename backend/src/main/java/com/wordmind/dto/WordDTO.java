package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "创建单词请求")
    public static class CreateRequest {
        @NotBlank(message = "单词不能为空")
        @Size(max = 100, message = "单词长度不能超过100字符")
        @Schema(description = "单词", example = "ephemeral", required = true)
        private String word;

        @Size(max = 100, message = "音标长度不能超过100字符")
        @Schema(description = "音标", example = "/ɪˈfemərəl/")
        private String phonetic;

        @Size(max = 50, message = "词性长度不能超过50字符")
        @Schema(description = "词性", example = "adj.", allowableValues = {"n.", "v.", "adj.", "adv.", "prep.", "conj.", "int."})
        private String pos;

        @NotBlank(message = "释义不能为空")
        @Size(max = 500, message = "释义长度不能超过500字符")
        @Schema(description = "中文释义", example = "短暂的，瞬息的", required = true)
        private String meaning;

        @Size(max = 1000, message = "例句长度不能超过1000字符")
        @Schema(description = "例句", example = "The ephemeral beauty of cherry blossoms.")
        private String example;

        @Size(max = 500, message = "记忆提示长度不能超过500字符")
        @Schema(description = "记忆技巧", example = "e(出)+phem(出现)+eral → 出现就消失 → 短暂的")
        private String memoryTip;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "更新单词请求")
    public static class UpdateRequest {
        @Size(max = 100, message = "单词长度不能超过100字符")
        @Schema(description = "单词", example = "ephemeral")
        private String word;

        @Size(max = 100, message = "音标长度不能超过100字符")
        @Schema(description = "音标", example = "/ɪˈfemərəl/")
        private String phonetic;

        @Size(max = 50, message = "词性长度不能超过50字符")
        @Schema(description = "词性", example = "adj.")
        private String pos;

        @Size(max = 500, message = "释义长度不能超过500字符")
        @Schema(description = "中文释义", example = "短暂的，瞬息的")
        private String meaning;

        @Size(max = 1000, message = "例句长度不能超过1000字符")
        @Schema(description = "例句", example = "The ephemeral beauty of cherry blossoms.")
        private String example;

        @Size(max = 500, message = "记忆提示长度不能超过500字符")
        @Schema(description = "记忆技巧", example = "e(出)+phem(出现)+eral → 出现就消失 → 短暂的")
        private String memoryTip;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单词响应")
    public static class Response {
        @Schema(description = "单词 ID", example = "1", required = true)
        private Long id;

        @Schema(description = "单词", example = "ephemeral", required = true)
        private String word;

        @Schema(description = "音标", example = "/ɪˈfemərəl/")
        private String phonetic;

        @Schema(description = "词性", example = "adj.")
        private String pos;

        @Schema(description = "中文释义", example = "短暂的，瞬息的", required = true)
        private String meaning;

        @Schema(description = "例句", example = "The ephemeral beauty of cherry blossoms.")
        private String example;

        @Schema(description = "记忆技巧", example = "e(出)+phem(出现)+eral → 出现就消失 → 短暂的")
        private String memoryTip;

        @Schema(description = "创建时间", example = "2024-01-15T10:30:00")
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单词列表响应")
    public static class ListResponse {
        @Schema(description = "单词列表", required = true)
        private java.util.List<Response> list;

        @Schema(description = "总数", example = "100", required = true)
        private Long total;

        @Schema(description = "当前页码", example = "1", required = true)
        private Integer page;

        @Schema(description = "每页大小", example = "20", required = true)
        private Integer size;
    }
}
