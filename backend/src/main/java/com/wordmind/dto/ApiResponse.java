package com.wordmind.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一 API 响应格式")
public class ApiResponse<T> {
    @Schema(description = "响应码，0 表示成功，非 0 表示错误", example = "0", required = true)
    private Integer code;

    @Schema(description = "响应消息", example = "ok", required = true)
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "追踪 ID，用于排查问题", example = "abc123xyz")
    private String traceId;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(0)
                .message("ok")
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success() {
        return success(null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(1)
                .message(message)
                .data(null)
                .build();
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
