package com.wordmind.algorithm;

import com.wordmind.entity.ReviewRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SM-2 Algorithm Tests")
class SM2AlgorithmTest {
    
    private SM2Algorithm algorithm;
    private Clock fixedClock;
    private LocalDateTime fixedNow;
    
    @BeforeEach
    void setUp() {
        algorithm = new SM2Algorithm();
        fixedNow = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        fixedClock = Clock.fixed(fixedNow.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
    }
    
    @Test
    @DisplayName("First review with KNOWN result should set interval to 1 day")
    void testFirstReview_Known() {
        ReviewRecord record = createNewRecord();
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.KNOWN)
                .now(fixedNow)
                .build();
        
        algorithm.calculate(context);
        
        assertEquals(1, record.getProficiency());
        assertEquals(1, record.getRepetitions());
        assertEquals(1, record.getIntervalDays());
        assertEquals(new BigDecimal("2.60"), record.getEf());
        assertEquals(fixedNow.plusDays(1), record.getNextReviewAt());
    }
    
    @Test
    @DisplayName("Second review with KNOWN result should set interval to 6 days")
    void testSecondReview_Known() {
        ReviewRecord record = createNewRecord();
        record.setProficiency(1);
        record.setRepetitions(1);
        record.setIntervalDays(1);
        record.setEf(new BigDecimal("2.60"));
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.KNOWN)
                .now(fixedNow)
                .build();
        
        algorithm.calculate(context);
        
        assertEquals(2, record.getProficiency());
        assertEquals(2, record.getRepetitions());
        assertEquals(6, record.getIntervalDays());
        assertEquals(new BigDecimal("2.70"), record.getEf());
        assertEquals(fixedNow.plusDays(6), record.getNextReviewAt());
    }
    
    @Test
    @DisplayName("Third review with KNOWN result should calculate interval as previous * EF")
    void testThirdReview_Known() {
        ReviewRecord record = createNewRecord();
        record.setProficiency(2);
        record.setRepetitions(2);
        record.setIntervalDays(6);
        record.setEf(new BigDecimal("2.70"));
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.KNOWN)
                .now(fixedNow)
                .build();
        
        algorithm.calculate(context);
        
        assertEquals(3, record.getProficiency());
        assertEquals(3, record.getRepetitions());
        assertEquals(17, record.getIntervalDays());
        assertEquals(new BigDecimal("2.80"), record.getEf());
        assertEquals(fixedNow.plusDays(17), record.getNextReviewAt());
    }
    
    @Test
    @DisplayName("UNKNOWN result should reset repetitions and set interval to 1 day")
    void testReview_Unknown() {
        ReviewRecord record = createNewRecord();
        record.setProficiency(3);
        record.setRepetitions(3);
        record.setIntervalDays(17);
        record.setEf(new BigDecimal("2.80"));
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.UNKNOWN)
                .now(fixedNow)
                .build();
        
        algorithm.calculate(context);
        
        assertEquals(0, record.getProficiency());
        assertEquals(0, record.getRepetitions());
        assertEquals(1, record.getIntervalDays());
        assertEquals(new BigDecimal("2.80"), record.getEf());
        assertEquals(fixedNow.plusDays(1), record.getNextReviewAt());
    }
    
    @Test
    @DisplayName("VAGUE result with q=3 should increase repetitions normally")
    void testReview_Vague() {
        ReviewRecord record = createNewRecord();
        record.setProficiency(1);
        record.setRepetitions(1);
        record.setIntervalDays(1);
        record.setEf(new BigDecimal("2.60"));
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.VAGUE)
                .now(fixedNow)
                .build();
        
        algorithm.calculate(context);
        
        assertEquals(0, record.getProficiency());
        assertEquals(2, record.getRepetitions());
        assertEquals(6, record.getIntervalDays());
        assertEquals(new BigDecimal("2.46"), record.getEf());
        assertEquals(fixedNow.plusDays(6), record.getNextReviewAt());
    }
    
    @Test
    @DisplayName("EF should not go below minimum 1.3")
    void testEF_Minimum() {
        ReviewRecord record = createNewRecord();
        record.setEf(new BigDecimal("1.30"));
        
        ReviewContext context = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.VAGUE)
                .now(fixedNow)
                .build();
        
        algorithm.calculate(context);
        
        assertEquals(new BigDecimal("1.30"), record.getEf());
    }
    
    @Test
    @DisplayName("Multiple consecutive KNOWN reviews should show exponential interval growth")
    void testConsecutiveKnownReviews() {
        ReviewRecord record = createNewRecord();
        
        LocalDateTime currentTime = fixedNow;
        
        for (int i = 1; i <= 5; i++) {
            ReviewContext context = ReviewContext.builder()
                    .record(record)
                    .result(ReviewRecord.ReviewResult.KNOWN)
                    .now(currentTime)
                    .build();
            
            algorithm.calculate(context);
            currentTime = record.getNextReviewAt();
        }
        
        assertEquals(5, record.getProficiency());
        assertEquals(5, record.getRepetitions());
        assertTrue(record.getIntervalDays() > 100,
                "After 5 consecutive KNOWN reviews, interval should be > 100 days, was: " + record.getIntervalDays());
        assertEquals(new BigDecimal("3.00"), record.getEf());
    }
    
    @Test
    @DisplayName("After forgetting, re-learning should start from day 1 but retain adjusted EF")
    void testForgettingAndRelearning() {
        ReviewRecord record = createNewRecord();
        
        ReviewContext knownContext = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.KNOWN)
                .now(fixedNow)
                .build();
        algorithm.calculate(knownContext);
        algorithm.calculate(knownContext);
        algorithm.calculate(knownContext);
        
        assertEquals(3, record.getRepetitions());
        assertEquals(17, record.getIntervalDays());
        assertEquals(new BigDecimal("2.80"), record.getEf());
        
        ReviewContext unknownContext = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.UNKNOWN)
                .now(fixedNow.plusDays(17))
                .build();
        algorithm.calculate(unknownContext);
        
        assertEquals(0, record.getRepetitions());
        assertEquals(1, record.getIntervalDays());
        assertEquals(new BigDecimal("2.80"), record.getEf(),
                "EF should be retained after forgetting");
        
        ReviewContext relearnContext = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.KNOWN)
                .now(fixedNow.plusDays(18))
                .build();
        algorithm.calculate(relearnContext);
        
        assertEquals(1, record.getRepetitions());
        assertEquals(1, record.getIntervalDays());
        assertEquals(new BigDecimal("2.90"), record.getEf());
    }
    
    @Test
    @DisplayName("Quality score mapping should be correct")
    void testQualityMapping() {
        ReviewRecord record = createNewRecord();
        
        ReviewContext knownContext = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.KNOWN)
                .now(fixedNow)
                .build();
        algorithm.calculate(knownContext);
        assertEquals(new BigDecimal("2.60"), record.getEf(),
                "KNOWN (q=5) should increase EF by 0.1");
        
        record = createNewRecord();
        ReviewContext vagueContext = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.VAGUE)
                .now(fixedNow)
                .build();
        algorithm.calculate(vagueContext);
        assertEquals(new BigDecimal("2.36"), record.getEf(),
                "VAGUE (q=3) should decrease EF by 0.14");
        
        record = createNewRecord();
        ReviewContext unknownContext = ReviewContext.builder()
                .record(record)
                .result(ReviewRecord.ReviewResult.UNKNOWN)
                .now(fixedNow)
                .build();
        algorithm.calculate(unknownContext);
        assertEquals(new BigDecimal("2.50"), record.getEf(),
                "UNKNOWN (q=0) should reset repetitions but keep initial EF");
    }
    
    private ReviewRecord createNewRecord() {
        ReviewRecord record = new ReviewRecord();
        record.setUserId(1L);
        record.setWordId(1L);
        return record;
    }
}
