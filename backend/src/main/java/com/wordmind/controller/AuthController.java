package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.AuthDTO;
import com.wordmind.security.JwtTokenProvider;
import com.wordmind.security.RateLimiter;
import com.wordmind.security.UserPrincipal;
import com.wordmind.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private RateLimiter rateLimiter;
    
    @PostMapping("/register")
    public ApiResponse<AuthDTO.RegisterResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }
    
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
