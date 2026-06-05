package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class QuizDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "测验开始响应")
    public static class StartResponse {
        @Schema(description = "测验ID", example = "quiz-20240115-abc123", required = true)
        private String quizId;

        @Schema(description = "测验题目列表", required = true)
        private List<Question> questions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "测验题目")
    public static class Question {
        @Schema(description = "单词ID", example = "3001", required = true)
        private Long wordId;

        @Schema(description = "单词", example = "serendipity", required = true)
        private String word;

        @Schema(description = "题目类型", example = "choice", allowableValues = {"choice", "fill", "match"}, required = true)
        private String type;

        @Schema(description = "题目内容", example = "单词 'serendipity' 的中文释义是？", required = true)
        private String question;

        @Schema(description = "选项列表", example = "[\"意外发现珍奇事物的本领\", \"悲伤的\", \"愤怒的\", \"快乐的\"]", required = true)
        private List<String> options;

        @Schema(description = "正确答案", example = "意外发现珍奇事物的本领", required = true)
        private String correctAnswer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提交测验答案请求")
    public static class SubmitRequest {
        @NotBlank(message = "测验ID不能为空")
        @Schema(description = "测验ID", example = "quiz-20240115-abc123", required = true)
        private String quizId;

        @NotNull(message = "答案不能为空")
        @Schema(description = "用户答案列表", required = true)
        private List<Answer> answers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户答案")
    public static class Answer {
        @Schema(description = "单词ID", example = "3001", required = true)
        private Long wordId;

        @Schema(description = "用户答案", example = "意外发现珍奇事物的本领", required = true)
        private String answer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "测验提交结果响应")
    public static class SubmitResponse {
        @Schema(description = "得分(0-100)", example = "90", required = true)
        private Integer score;

        @Schema(description = "答对题数", example = "9", required = true)
        private Integer correctCount;

        @Schema(description = "总题数", example = "10", required = true)
        private Integer totalCount;

        @Schema(description = "答题用时(秒)", example = "180", required = true)
        private Integer duration;

        @Schema(description = "答错的单词列表", required = true)
        private List<WordDTO.Response> wrongWords;
    }
}
