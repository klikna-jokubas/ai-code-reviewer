package com.jokubas.aicodereviewer.service;

import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final AnalyzerClient analyzerClient;

    public ReviewService(AnalyzerClient analyzerClient) {
        this.analyzerClient = analyzerClient;
    }

    public ReviewResponse review(ReviewRequest request) {

        String code = request.code();
        String language = request.language();

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }

        if (code.length() > 10_000) {
            throw new IllegalArgumentException("Code is too long");
        }

        if (!request.language().equalsIgnoreCase("java")
                && !request.language().equalsIgnoreCase("python")) {
            throw new IllegalArgumentException("Unsupported language");
        }

        return analyzerClient.analyze(code, language);
    }
}
