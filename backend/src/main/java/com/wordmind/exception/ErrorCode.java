package com.wordmind.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    WORD_NOT_FOUND(1001, "word.not.found"),
    REVIEW_RESULT_INVALID(1002, "review.result.invalid"),
    REVIEW_RECORD_NOT_FOUND(1003, "review.record.not.found"),
    ALGORITHM_NOT_SUPPORTED(1004, "algorithm.not.supported");
    
    private final int code;
    private final String messageKey;
    
    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
