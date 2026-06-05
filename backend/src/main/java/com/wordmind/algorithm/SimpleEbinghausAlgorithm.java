package com.wordmind.algorithm;

import com.wordmind.entity.ReviewRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SimpleEbinghausAlgorithm implements ReviewAlgorithm {
    
    public static final String NAME = "SIMPLE_EBINGHAUS";
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public void calculate(ReviewContext context) {
        ReviewRecord record = context.getRecord();
        ReviewRecord.ReviewResult result = context.getResult();
        LocalDateTime now = context.getNow();
        
        int newProficiency = calculateProficiency(record.getProficiency(), result);
        record.setProficiency(newProficiency);
        record.setNextReviewAt(calculateNextReviewTime(newProficiency, now));
    }
    
    private LocalDateTime calculateNextReviewTime(int proficiency, LocalDateTime now) {
        switch (proficiency) {
            case 0:
                return now.plusMinutes(5);
            case 1:
                return now.plusHours(1);
            case 2:
                return now.plusDays(1);
            case 3:
                return now.plusDays(3);
            case 4:
                return now.plusDays(7);
            case 5:
                return now.plusDays(30);
            default:
                return now.plusDays(1);
        }
    }
}
