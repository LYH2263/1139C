package com.wordmind.service;

import com.wordmind.dto.ReviewDTO;
import com.wordmind.entity.ReviewRecord;
import com.wordmind.entity.Word;
import com.wordmind.repository.ReviewRecordRepository;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRecordRepository reviewRecordRepository;
    
    @Autowired
    private WordRepository wordRepository;
    
    public ReviewDTO.TodayResponse getTodayReviews(Long userId) {
        List<ReviewRecord> records = reviewRecordRepository.findTodayReviews(userId, LocalDateTime.now());
        
        List<ReviewDTO.Response> list = records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ReviewDTO.TodayResponse.builder()
                .list(list)
                .total((long) list.size())
                .build();
    }
    
    @Transactional
    public ReviewDTO.Response submitReview(Long userId, ReviewDTO.SubmitRequest request) {
        Word word = wordRepository.findById(request.getWordId())
                .orElseThrow(() -> new RuntimeException("单词不存在"));
        
        ReviewRecord.ReviewResult result = ReviewRecord.ReviewResult.valueOf(request.getResult());
        
        Optional<ReviewRecord> existing = reviewRecordRepository.findByUserIdAndWordId(userId, request.getWordId());
        
        ReviewRecord record;
        if (existing.isPresent()) {
            record = existing.get();
            record.setResult(result);
            record.setProficiency(calculateProficiency(record.getProficiency(), result));
        } else {
            record = new ReviewRecord();
            record.setUserId(userId);
            record.setWordId(request.getWordId());
            record.setResult(result);
            record.setProficiency(calculateProficiency(0, result));
        }
        
        record.setNextReviewAt(calculateNextReviewTime(record.getProficiency()));
        ReviewRecord saved = reviewRecordRepository.save(record);
        
        return convertToDTO(saved);
    }
    
    private int calculateProficiency(int current, ReviewRecord.ReviewResult result) {
        switch (result) {
            case KNOWN: return Math.min(current + 1, 5);
            case VAGUE: return Math.max(current - 1, 0);
            case UNKNOWN: return 0;
            default: return current;
        }
    }
    
    private LocalDateTime calculateNextReviewTime(int proficiency) {
        LocalDateTime now = LocalDateTime.now();
        switch (proficiency) {
            case 0: return now.plusMinutes(5);
            case 1: return now.plusHours(1);
            case 2: return now.plusDays(1);
            case 3: return now.plusDays(3);
            case 4: return now.plusDays(7);
            case 5: return now.plusDays(30);
            default: return now.plusDays(1);
        }
    }
    
    private ReviewDTO.Response convertToDTO(ReviewRecord record) {
        Word word = wordRepository.findById(record.getWordId()).orElse(null);
        
        return ReviewDTO.Response.builder()
                .id(record.getId())
                .wordId(record.getWordId())
                .word(word != null ? word.getWord() : "")
                .meaning(word != null ? word.getMeaning() : "")
                .result(record.getResult().name())
                .proficiency(record.getProficiency())
                .nextReviewAt(record.getNextReviewAt())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
