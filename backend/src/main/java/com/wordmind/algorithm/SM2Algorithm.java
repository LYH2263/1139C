package com.wordmind.algorithm;

import com.wordmind.entity.ReviewRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class SM2Algorithm implements ReviewAlgorithm {
    
    public static final String NAME = "SM2";
    
    private static final BigDecimal MIN_EF = new BigDecimal("1.3");
    private static final BigDecimal EF_CONSTANT_1 = new BigDecimal("0.1");
    private static final BigDecimal EF_CONSTANT_2 = new BigDecimal("0.08");
    private static final BigDecimal EF_CONSTANT_3 = new BigDecimal("0.02");
    private static final int FIRST_INTERVAL_DAYS = 1;
    private static final int SECOND_INTERVAL_DAYS = 6;
    
    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public void calculate(ReviewContext context) {
        ReviewRecord record = context.getRecord();
        ReviewRecord.ReviewResult result = context.getResult();
        LocalDateTime now = context.getNow();
        
        int quality = mapResultToQuality(result);
        int newProficiency = calculateProficiency(record.getProficiency(), result);
        record.setProficiency(newProficiency);
        
        if (quality < 3) {
            record.setRepetitions(0);
            record.setIntervalDays(FIRST_INTERVAL_DAYS);
            record.setNextReviewAt(now.plusDays(FIRST_INTERVAL_DAYS));
            return;
        }
        
        BigDecimal newEf = calculateNewEF(record.getEf(), quality);
        record.setEf(newEf);
        
        int newRepetitions = record.getRepetitions() + 1;
        record.setRepetitions(newRepetitions);
        
        int newInterval = calculateInterval(newRepetitions, record.getIntervalDays(), newEf);
        record.setIntervalDays(newInterval);
        record.setNextReviewAt(now.plusDays(newInterval));
    }
    
    private int mapResultToQuality(ReviewRecord.ReviewResult result) {
        switch (result) {
            case KNOWN:
                return 5;
            case VAGUE:
                return 3;
            case UNKNOWN:
                return 0;
            default:
                return 0;
        }
    }
    
    private BigDecimal calculateNewEF(BigDecimal currentEf, int quality) {
        BigDecimal fiveMinusQ = BigDecimal.valueOf(5 - quality);
        BigDecimal term = EF_CONSTANT_1.subtract(
                fiveMinusQ.multiply(
                        EF_CONSTANT_2.add(fiveMinusQ.multiply(EF_CONSTANT_3))
                )
        );
        BigDecimal newEf = currentEf.add(term).setScale(2, RoundingMode.HALF_UP);
        return newEf.max(MIN_EF);
    }
    
    private int calculateInterval(int repetitions, int previousInterval, BigDecimal ef) {
        if (repetitions == 1) {
            return FIRST_INTERVAL_DAYS;
        } else if (repetitions == 2) {
            return SECOND_INTERVAL_DAYS;
        } else {
            return BigDecimal.valueOf(previousInterval)
                    .multiply(ef)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
        }
    }
}
