package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.MindMapDTO;
import com.wordmind.dto.WordDTO;
import com.wordmind.service.RelationService;
import com.wordmind.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private WordService wordService;
    
    @Autowired
    private RelationService relationService;
    
    @PostMapping("/words")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<WordDTO.Response> createWord(@Valid @RequestBody WordDTO.CreateRequest request) {
        return ApiResponse.success(wordService.createWord(request));
    }
    
    @PutMapping("/words/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<WordDTO.Response> updateWord(
            @PathVariable Long id,
            @Valid @RequestBody WordDTO.UpdateRequest request) {
        return ApiResponse.success(wordService.updateWord(id, request));
    }
    
    @DeleteMapping("/words/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ApiResponse.success();
    }
    
    @PostMapping("/words/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<? > importWords(@RequestParam("file") MultipartFile file) {
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
    
    @PostMapping("/relations")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MindMapDTO.RelationEdge> createRelation(@Valid @RequestBody MindMapDTO.RelationRequest request) {
        return ApiResponse.success(relationService.createRelation(request));
    }
    
    @DeleteMapping("/relations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteRelation(@PathVariable Long id) {
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
