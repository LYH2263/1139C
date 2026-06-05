package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AuthDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户注册请求")
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度必须在3-20字符之间")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
        @Schema(description = "用户名", example = "john_doe", required = true)
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 32, message = "密码长度必须在6-32字符之间")
        @Schema(description = "密码", example = "password123", required = true)
        private String password;

        @Email(message = "邮箱格式不正确")
        @Schema(description = "邮箱地址", example = "john@example.com")
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户登录请求")
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        @Schema(description = "用户名", example = "john_doe", required = true)
        private String username;

        @NotBlank(message = "密码不能为空")
        @Schema(description = "密码", example = "password123", required = true)
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "登录响应")
    public static class LoginResponse {
        @Schema(description = "JWT 访问令牌", example = "eyJhbGciOiJIUzUxMiJ9...", required = true)
        private String token;

        @Schema(description = "用户信息", required = true)
        private UserInfo user;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户 ID", example = "1", required = true)
        private Long id;

        @Schema(description = "用户名", example = "john_doe", required = true)
        private String username;

        @Schema(description = "邮箱地址", example = "john@example.com")
        private String email;

        @Schema(description = "用户角色", example = "USER", allowableValues = {"USER", "ADMIN"}, required = true)
        private String role;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "注册响应")
    public static class RegisterResponse {
        @Schema(description = "用户 ID", example = "1", required = true)
        private Long userId;

        @Schema(description = "用户名", example = "john_doe", required = true)
        private String username;

        @Schema(description = "邮箱地址", example = "john@example.com")
        private String email;

        @Schema(description = "用户角色", example = "USER", required = true)
        private String role;
    }
}
