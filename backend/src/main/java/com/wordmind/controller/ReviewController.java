package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.ReviewDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<ReviewDTO.TodayResponse> getTodayReviews(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(reviewService.getTodayReviews(userId));
    }
    
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
