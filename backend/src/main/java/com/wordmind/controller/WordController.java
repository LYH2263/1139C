package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.WordDTO;
import com.wordmind.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Words", description = "单词管理相关接口，包括单词列表查询和单词详情获取")
@RestController
@RequestMapping("/api/words")
public class WordController {
    
    @Autowired
    private WordService wordService;
    
    @Operation(summary = "获取单词列表", description = "分页查询单词列表，支持按关键词和词性筛选")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<WordDTO.ListResponse> getWords(
            @Parameter(description = "搜索关键词，匹配单词或释义", example = "apple") @RequestParam(required = false) String keyword,
            @Parameter(description = "词性筛选，如 n., v., adj. 等", example = "n.") @RequestParam(required = false) String pos,
            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数", example = "20") @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(wordService.getWords(keyword, pos, page, size));
    }
    
    @Operation(summary = "获取单词详情", description = "根据单词ID获取单词的详细信息，包括释义、例句等")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<WordDTO.Response> getWordById(
            @Parameter(description = "单词ID", example = "1") @PathVariable Long id) {
        return ApiResponse.success(wordService.getWordById(id));
    }
}
