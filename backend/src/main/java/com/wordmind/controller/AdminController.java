package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.MindMapDTO;
import com.wordmind.dto.WordDTO;
import com.wordmind.service.RelationService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Admin", description = "管理员相关接口，包括单词的增删改查、批量导入和单词关系管理，需要管理员权限")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private WordService wordService;
    
    @Autowired
    private RelationService relationService;
    
    @Operation(summary = "创建单词", description = "创建新的单词词条，包括单词、释义、例句等信息")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/words")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<WordDTO.Response> createWord(@Valid @RequestBody WordDTO.CreateRequest request) {
        return ApiResponse.success(wordService.createWord(request));
    }
    
    @Operation(summary = "更新单词", description = "更新指定ID的单词信息")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PutMapping("/words/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<WordDTO.Response> updateWord(
            @Parameter(description = "单词ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody WordDTO.UpdateRequest request) {
        return ApiResponse.success(wordService.updateWord(id, request));
    }
    
    @Operation(summary = "删除单词", description = "删除指定ID的单词")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @DeleteMapping("/words/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteWord(
            @Parameter(description = "单词ID", example = "1") @PathVariable Long id) {
        wordService.deleteWord(id);
        return ApiResponse.success();
    }
    
    @Operation(summary = "批量导入单词", description = "通过CSV文件批量导入单词，文件格式：第一行为表头，数据列依次为：word,meaning,example。系统将自动处理每一行数据并创建单词词条，返回导入成功数量和失败行信息")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "导入完成", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/words/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<? > importWords(
            @Parameter(description = "CSV格式单词文件，表头：word,meaning,example", example = "words.csv") @RequestParam("file") MultipartFile file) {
        try {
            List<WordDTO.CreateRequest> words = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            boolean firstLine = true;
            int importedCount = 0;
            List<String> failedRows = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        WordDTO.CreateRequest request = WordDTO.CreateRequest.builder()
                                .word(parts[0].trim())
                                .meaning(parts[1].trim())
                                .example(parts.length > 2 ? parts[2].trim() : null)
                                .build();
                        wordService.createWord(request);
                        importedCount++;
                    } catch (Exception e) {
                        failedRows.add(line + " - " + e.getMessage());
                    }
                } else {
                    failedRows.add(line + " - 格式错误");
                }
            }
            
            return ApiResponse.success(new ImportResult(importedCount, failedRows));
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "创建单词关联关系", description = "创建两个单词之间的关联关系，用于思维导图展示")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "BadRequest"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @PostMapping("/relations")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MindMapDTO.RelationEdge> createRelation(@Valid @RequestBody MindMapDTO.RelationRequest request) {
        return ApiResponse.success(relationService.createRelation(request));
    }
    
    @Operation(summary = "删除单词关联关系", description = "删除指定ID的单词关联关系")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "删除成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", ref = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", ref = "Forbidden"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", ref = "NotFound"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    @DeleteMapping("/relations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteRelation(
            @Parameter(description = "关联关系ID", example = "1") @PathVariable Long id) {
        relationService.deleteRelation(id);
        return ApiResponse.success();
    }
    
    private static class ImportResult {
        public final int importedCount;
        public final List<String> failedRows;
        
        public ImportResult(int importedCount, List<String> failedRows) {
            this.importedCount = importedCount;
            this.failedRows = failedRows;
        }
    }
}
