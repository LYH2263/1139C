package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.MindMapDTO;
import com.wordmind.service.MindMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mindmap")
public class MindMapController {
    
    @Autowired
    private MindMapService mindMapService;
    
    @GetMapping("/{wordId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<MindMapDTO.Response> getMindMap(
            @PathVariable Long wordId,
            @RequestParam(defaultValue = "1") int depth) {
        return ApiResponse.success(mindMapService.getMindMap(wordId, depth));
    }
}
