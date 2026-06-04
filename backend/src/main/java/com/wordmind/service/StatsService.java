package com.wordmind.service;

import com.wordmind.dto.StatsDTO;
import com.wordmind.dto.StudyPlanDTO;
import com.wordmind.entity.StudyPlan;
import com.wordmind.entity.Word;
import com.wordmind.repository.QuizRecordRepository;
import com.wordmind.repository.ReviewRecordRepository;
import com.wordmind.repository.StudyPlanRepository;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsService {
    
    @Autowired
    private ReviewRecordRepository reviewRecordRepository;
    
    @Autowired
    private QuizRecordRepository quizRecordRepository;
    
    @Autowired
    private StudyPlanRepository studyPlanRepository;
    
    @Autowired
    private WordRepository wordRepository;
    
    public StatsDTO.Response getUserStats(Long userId) {
        Long totalWords = reviewRecordRepository.countKnownWordsByUserId(userId);
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long todayReviewCount = reviewRecordRepository.countTodayReviewsByUserId(userId, startOfDay, endOfDay);
        Double avgScore = quizRecordRepository.calculateAverageScoreByUserId(userId);
        
        return StatsDTO.Response.builder()
                .totalWords(totalWords != null ? totalWords : 0L)
                .todayReviewCount(todayReviewCount != null ? todayReviewCount : 0L)
                .accuracy(avgScore != null ? avgScore : 0.0)
                .streakDays(calculateStreakDays(userId))
                .build();
    }
    
    private Integer calculateStreakDays(Long userId) {
        // 简化版：返回最近连续学习天数（实际应该根据学习记录计算）
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long todayCount = reviewRecordRepository.countTodayReviewsByUserId(userId, startOfDay, endOfDay);
        return todayCount != null && todayCount > 0 ? 1 : 0;
    }
    
    @Transactional
    public StudyPlanDTO.Response createStudyPlan(Long userId, StudyPlanDTO.CreateRequest request) {
        if (studyPlanRepository.existsByUserIdAndWordId(userId, request.getWordId())) {
            throw new RuntimeException("该单词已在学习计划中");
        }
        
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setWordId(request.getWordId());
        plan.setPlanType(StudyPlan.PlanType.valueOf(request.getPlanType()));
        
        StudyPlan saved = studyPlanRepository.save(plan);
        return convertToDTO(saved);
    }
    
    public List<StudyPlanDTO.Response> getUserStudyPlans(Long userId) {
        return studyPlanRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private StudyPlanDTO.Response convertToDTO(StudyPlan plan) {
        Word word = wordRepository.findById(plan.getWordId()).orElse(null);
        
        return StudyPlanDTO.Response.builder()
                .id(plan.getId())
                .wordId(plan.getWordId())
                .word(word != null ? word.getWord() : "")
                .meaning(word != null ? word.getMeaning() : "")
                .planType(plan.getPlanType().name())
                .createdAt(plan.getCreatedAt().toString())
                .build();
    }
}
