package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.StatsDTO;
import com.wordmind.dto.StudyPlanDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.service.StatsService;
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
import java.util.List;

@Tag(name = "Stats", description = "学习统计和计划相关接口，包括个人学习数据统计、学习计划创建和学习记录查询")
@RestController
@RequestMapping("/api")
public class StatsController {
    
    @Autowired
    private StatsService statsService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Operation(summary = "获取个人学习统计", description = "获取当前用户的学习统计数据，包括已学习单词数、复习次数、正确率等")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @GetMapping("/stats/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<StatsDTO.Response> getMyStats(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(statsService.getUserStats(userId));
    }
    
    @Operation(summary = "创建学习计划", description = "创建新的学习计划，设置每日学习目标和学习周期")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/study-plans")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<StudyPlanDTO.Response> createStudyPlan(
            HttpServletRequest request,
            @Valid @RequestBody StudyPlanDTO.CreateRequest createRequest) {
        Long userId = getUserIdFromRequest(request);
        return ApiResponse.success(statsService.createStudyPlan(userId, createRequest));
    }
    
    @Operation(summary = "获取学习记录", description = "获取当前用户的所有学习计划和学习记录列表")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
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
