package com.jokubas.aicodereviewer.controller;

import com.jokubas.aicodereviewer.service.ReviewRequest;
import com.jokubas.aicodereviewer.service.ReviewResponse;
import com.jokubas.aicodereviewer.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    void reviewEndpointAcceptsValidRequest() throws Exception {

        when(reviewService.review(any(ReviewRequest.class)))
                .thenReturn(new ReviewResponse(List.of()));

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "code": "System.out.println(123);",
                                    "language": "java"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void reviewEndpointRejectsInvalidJson() throws Exception {
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "code": "hello"
                                "language": "java"
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reviewEndpointReturnsBadRequestForInvalidCode() throws Exception {

        when(reviewService.review(any(ReviewRequest.class)))
                .thenThrow(new IllegalArgumentException("Code cannot be null or empty"));

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "code": "",
                                "language": "java"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                    {
                        "error": "Code cannot be null or empty"
                    }
                    """));
    }
}