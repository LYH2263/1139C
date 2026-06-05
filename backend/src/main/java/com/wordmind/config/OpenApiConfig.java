package com.wordmind.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI wordMindOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WordMind API")
                        .description("单词记忆与思维导图学习平台 API 文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WordMind Team")
                                .email("support@wordmind.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addResponses("Unauthorized", createUnauthorizedResponse())
                        .addResponses("Forbidden", createForbiddenResponse())
                        .addResponses("NotFound", createNotFoundResponse())
                        .addResponses("BadRequest", createBadRequestResponse())
                        .addResponses("InternalServerError", createInternalServerErrorResponse()));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi wordsApi() {
        return GroupedOpenApi.builder()
                .group("Words")
                .pathsToMatch("/api/words/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mindMapApi() {
        return GroupedOpenApi.builder()
                .group("MindMap")
                .pathsToMatch("/api/mindmap/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reviewApi() {
        return GroupedOpenApi.builder()
                .group("Review")
                .pathsToMatch("/api/reviews/**")
                .build();
    }

    @Bean
    public GroupedOpenApi quizApi() {
        return GroupedOpenApi.builder()
                .group("Quiz")
                .pathsToMatch("/api/quiz/**")
                .build();
    }

    @Bean
    public GroupedOpenApi statsApi() {
        return GroupedOpenApi.builder()
                .group("Stats")
                .pathsToMatch("/api/stats/**", "/api/study-plans/**", "/api/study-records/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi healthApi() {
        return GroupedOpenApi.builder()
                .group("Health")
                .pathsToMatch("/api/health")
                .build();
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
                .description("未授权 - 需要有效的 JWT Token")
                .content(createErrorContent(401, "未提供有效的认证令牌"));
    }

    private ApiResponse createForbiddenResponse() {
        return new ApiResponse()
                .description("禁止访问 - 权限不足")
                .content(createErrorContent(403, "没有权限执行此操作"));
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
                .description("资源不存在")
                .content(createErrorContent(404, "请求的资源不存在"));
    }

    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
                .description("请求参数错误")
                .content(createErrorContent(400, "请求参数验证失败"));
    }

    private ApiResponse createInternalServerErrorResponse() {
        return new ApiResponse()
                .description("服务器内部错误")
                .content(createErrorContent(500, "服务器内部错误，请稍后重试"));
    }

    private Content createErrorContent(int code, String message) {
        Map<String, Object> errorProperties = new HashMap<>();
        errorProperties.put("code", new Schema<Integer>().example(code).type("integer"));
        errorProperties.put("message", new Schema<String>().example(message).type("string"));
        errorProperties.put("data", new Schema<>().type("object").nullable(true));
        errorProperties.put("traceId", new Schema<String>().example("abc123xyz").type("string"));

        Schema<?> errorSchema = new Schema<>()
                .type("object")
                .properties(errorProperties);

        return new Content()
                .addMediaType("application/json",
                        new MediaType().schema(errorSchema));
    }
}
