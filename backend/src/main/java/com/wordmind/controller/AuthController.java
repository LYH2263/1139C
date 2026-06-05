package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.AuthDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.security.RateLimiter;
import com.wordmind.security.UserPrincipal;
import com.wordmind.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "Auth", description = "用户认证相关接口，包括注册、登录和获取当前用户信息")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private RateLimiter rateLimiter;
    
    @Operation(summary = "用户注册", description = "新用户注册账号，需要提供用户名、邮箱和密码")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/register")
    public ApiResponse<AuthDTO.RegisterResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }
    
    @Operation(summary = "用户登录", description = "用户使用用户名和密码登录，登录成功后返回 JWT Token。系统有登录失败次数限制，超过5次将被锁定5分钟。")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/login")
    public ApiResponse<AuthDTO.LoginResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String ip = getClientIP(httpRequest);
        
        // 检查是否被锁定
        if (rateLimiter.isBlocked(ip)) {
            return ApiResponse.<AuthDTO.LoginResponse>builder()
                    .code(429)
                    .message("登录尝试过多，请5分钟后再试")
                    .data(null)
                    .build();
        }
        
        try {
            AuthDTO.LoginResponse response = authService.login(request);
            rateLimiter.resetAttempts(ip);  // 登录成功，重置计数
            return ApiResponse.success(response);
        } catch (BadCredentialsException e) {
            rateLimiter.recordFailedAttempt(ip);  // 记录失败
            int remaining = rateLimiter.getRemainingAttempts(ip);
            return ApiResponse.<AuthDTO.LoginResponse>builder()
                    .code(401)
                    .message("用户名或密码错误，剩余尝试次数: " + remaining)
                    .data(null)
                    .build();
        }
    }
    
    @Operation(summary = "获取当前用户信息", description = "根据请求头中的 JWT Token 获取当前登录用户的详细信息")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<AuthDTO.UserInfo> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未提供有效的认证令牌");
        }
        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);
        return ApiResponse.success(authService.getCurrentUser(userId));
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
