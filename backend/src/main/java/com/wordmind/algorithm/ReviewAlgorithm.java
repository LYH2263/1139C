package com.wordmind.algorithm;

import com.wordmind.entity.ReviewRecord;

public interface ReviewAlgorithm {
    
    String getName();
    
    void calculate(ReviewContext context);
    
    default int calculateProficiency(int currentProficiency, ReviewRecord.ReviewResult result) {
        switch (result) {
            case KNOWN:
                return Math.min(currentProficiency + 1, 5);
            case VAGUE:
                return Math.max(currentProficiency - 1, 0);
            case UNKNOWN:
                return 0;
            default:
                return currentProficiency;
        }
    }
}
