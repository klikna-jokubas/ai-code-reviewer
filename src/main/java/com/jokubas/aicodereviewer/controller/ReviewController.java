package com.jokubas.aicodereviewer.controller;

import com.jokubas.aicodereviewer.service.ReviewRequest;
import com.jokubas.aicodereviewer.service.ReviewResponse;
import com.jokubas.aicodereviewer.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewResponse review(@RequestBody ReviewRequest request) {
        return reviewService.review(request);
    }
}