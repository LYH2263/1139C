package com.wordmind.algorithm;

import com.wordmind.entity.ReviewRecord;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewContext {
    private ReviewRecord record;
    private ReviewRecord.ReviewResult result;
    private LocalDateTime now;
}
