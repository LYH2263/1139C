package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.MindMapDTO;
import com.wordmind.service.MindMapService;
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

@Tag(name = "MindMap", description = "思维导图相关接口，用于获取单词之间的关联关系")
@RestController
@RequestMapping("/api/mindmap")
public class MindMapController {
    
    @Autowired
    private MindMapService mindMapService;
    
    @Operation(summary = "获取单词思维导图", description = "根据单词ID获取该单词的思维导图，展示相关联的单词网络结构，可指定遍历深度")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @GetMapping("/{wordId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<MindMapDTO.Response> getMindMap(
            @Parameter(description = "单词ID", example = "1") @PathVariable Long wordId,
            @Parameter(description = "思维导图遍历深度", example = "2") @RequestParam(defaultValue = "1") int depth) {
        return ApiResponse.success(mindMapService.getMindMap(wordId, depth));
    }
}
