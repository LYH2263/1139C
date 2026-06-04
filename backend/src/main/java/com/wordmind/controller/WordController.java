package com.wordmind.controller;

import com.wordmind.dto.ApiResponse;
import com.wordmind.dto.WordDTO;
import com.wordmind.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/words")
public class WordController {
    
    @Autowired
    private WordService wordService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<WordDTO.ListResponse> getWords(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String pos,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(wordService.getWords(keyword, pos, page, size));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<WordDTO.Response> getWordById(@PathVariable Long id) {
        return ApiResponse.success(wordService.getWordById(id));
    }
}
