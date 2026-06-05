package com.wordmind.service;

import com.wordmind.algorithm.ReviewAlgorithm;
import com.wordmind.algorithm.ReviewContext;
import com.wordmind.dto.ReviewDTO;
import com.wordmind.entity.ReviewRecord;
import com.wordmind.entity.Word;
import com.wordmind.exception.BusinessException;
import com.wordmind.exception.ErrorCode;
import com.wordmind.repository.ReviewRecordRepository;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRecordRepository reviewRecordRepository;
    
    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private ReviewAlgorithm reviewAlgorithm;
    
    @Autowired
    private Clock clock;
    
    @Transactional(readOnly = true)
    public ReviewDTO.TodayResponse getTodayReviews(Long userId) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<ReviewRecord> records = reviewRecordRepository.findTodayReviews(userId, now);
        
        if (records.isEmpty()) {
            return ReviewDTO.TodayResponse.builder()
                    .list(List.of())
                    .total(0L)
                    .build();
        }
        
        List<Long> wordIds = records.stream()
                .map(ReviewRecord::getWordId)
                .distinct()
                .collect(Collectors.toList());
        
        List<Word> words = wordRepository.findByIdIn(wordIds);
        Map<Long, Word> wordMap = words.stream()
                .collect(Collectors.toMap(Word::getId, Function.identity()));
        
        List<ReviewDTO.Response> list = records.stream()
                .map(record -> convertToDTO(record, wordMap))
                .collect(Collectors.toList());
        
        return ReviewDTO.TodayResponse.builder()
                .list(list)
                .total((long) list.size())
                .build();
    }
    
    @Transactional
    public ReviewDTO.Response submitReview(Long userId, ReviewDTO.SubmitRequest request) {
        Word word = findWordById(request.getWordId());
        
        ReviewRecord.ReviewResult result = parseReviewResult(request.getResult());
        
        Optional<ReviewRecord> existing = reviewRecordRepository.findByUserIdAndWordId(userId, request.getWordId());
        
        ReviewRecord record;
        if (existing.isPresent()) {
            record = existing.get();
        } else {
            record = new ReviewRecord();
            record.setUserId(userId);
            record.setWordId(request.getWordId());
        }
        
        record.setResult(result);
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(result)
                .now(LocalDateTime.now(clock))
                .build();
        
        reviewAlgorithm.calculate(context);
        
        ReviewRecord saved = reviewRecordRepository.save(record);
        
        return convertToDTO(saved, word);
    }
    
    @Transactional(readOnly = true)
    public Word findWordById(Long wordId) {
        return wordRepository.findById(wordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORD_NOT_FOUND, wordId));
    }
    
    private ReviewRecord.ReviewResult parseReviewResult(String result) {
        try {
            return ReviewRecord.ReviewResult.valueOf(result);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.REVIEW_RESULT_INVALID, result);
        }
    }
    
    private ReviewDTO.Response convertToDTO(ReviewRecord record, Map<Long, Word> wordMap) {
        Word word = wordMap.get(record.getWordId());
        return buildResponse(record, word);
    }
    
    private ReviewDTO.Response convertToDTO(ReviewRecord record, Word word) {
        return buildResponse(record, word);
    }
    
    private ReviewDTO.Response buildResponse(ReviewRecord record, Word word) {
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
