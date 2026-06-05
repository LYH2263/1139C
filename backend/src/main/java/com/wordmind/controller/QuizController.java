package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.QuizDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Quiz", description = "单词测验相关接口，包括开始测验和提交答案")
@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Operation(summary = "开始单词测验", description = "开始一次新的单词测验，系统将随机抽取指定数量的单词生成测验题目")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "测验创建成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<QuizDTO.StartResponse> startQuiz(
            HttpServletRequest request,
            @Parameter(description = "测验题目数量", example = "10") @RequestParam(defaultValue = "10") int count) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(quizService.startQuiz(userId, count));
    }
    
    @Operation(summary = "提交测验答案", description = "提交测验的所有答案，系统将自动评分并返回测验结果，包括正确数、错误数和正确率")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "提交成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<QuizDTO.SubmitResponse> submitQuiz(
            HttpServletRequest request,
            @Valid @RequestBody QuizDTO.SubmitRequest submitRequest) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(quizService.submitQuiz(userId, submitRequest));
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return tokenProvider.getUserIdFromToken(token);
    }
}
