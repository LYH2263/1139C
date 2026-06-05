package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.ReviewDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Review", description = "单词复习相关接口，包括获取今日复习任务和提交复习结果")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Operation(summary = "获取今日复习任务", description = "获取当前用户今日需要复习的单词列表，基于艾宾浩斯遗忘曲线算法")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<ReviewDTO.TodayResponse> getTodayReviews(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(reviewService.getTodayReviews(userId));
    }
    
    @Operation(summary = "提交复习结果", description = "提交单词复习的结果，包括记忆质量评分，系统将根据评分更新下次复习时间")
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
    public ApiResponse<ReviewDTO.Response> submitReview(
            HttpServletRequest request,
            @Valid @RequestBody ReviewDTO.SubmitRequest submitRequest) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(reviewService.submitReview(userId, submitRequest));
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return tokenProvider.getUserIdFromToken(token);
    }
}
