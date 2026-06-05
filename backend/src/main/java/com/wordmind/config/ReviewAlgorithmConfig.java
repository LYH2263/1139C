package com.wordmind.config;

import com.wordmind.algorithm.ReviewAlgorithm;
import com.wordmind.algorithm.SM2Algorithm;
import com.wordmind.algorithm.SimpleEbinghausAlgorithm;
import com.wordmind.exception.BusinessException;
import com.wordmind.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class ReviewAlgorithmConfig {
    
    @Value("${review.algorithm:SM2}")
    private String algorithmName;
    
    @Bean
    @Primary
    public ReviewAlgorithm reviewAlgorithm(List<ReviewAlgorithm> algorithms) {
        return algorithms.stream()
                .filter(alg -> alg.getName().equalsIgnoreCase(algorithmName))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ALGORITHM_NOT_SUPPORTED, algorithmName));
    }
}
