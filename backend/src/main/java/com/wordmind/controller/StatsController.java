package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.StatsDTO;
import com.wordmind.dto.StudyPlanDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StatsController {
    
    @Autowired
    private StatsService statsService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @GetMapping("/stats/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<StatsDTO.Response> getMyStats(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(statsService.getUserStats(userId));
    }
    
    @PostMapping("/study-plans")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<StudyPlanDTO.Response> createStudyPlan(
            HttpServletRequest request,
            @Valid @RequestBody StudyPlanDTO.CreateRequest createRequest) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(statsService.createStudyPlan(userId, createRequest));
    }
    
    @GetMapping("/study-records")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<StudyPlanDTO.Response>> getStudyRecords(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(statsService.getUserStudyPlans(userId));
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return tokenProvider.getUserIdFromToken(token);
    }
}
