package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.QuizDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<QuizDTO.StartResponse> startQuiz(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int count) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(quizService.startQuiz(userId, count));
    }
    
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
