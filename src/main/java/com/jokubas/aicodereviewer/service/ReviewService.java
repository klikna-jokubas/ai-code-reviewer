package com.jokubas.aicodereviewer.service;

import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final AnalyzerClient analyzerClient;

    public ReviewService(AnalyzerClient analyzerClient) {
        this.analyzerClient = analyzerClient;
    }

    public String review(String code) {
        return analyzerClient.analyze(code);
    }
}
