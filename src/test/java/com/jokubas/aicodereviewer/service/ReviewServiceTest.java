package com.jokubas.aicodereviewer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Test
    void rejectsEmptyCode() {
        ReviewService service = new ReviewService(null);

        ReviewRequest request = new ReviewRequest("", "java");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.review(request)
        );
    }

    @Test
    void rejectsUnsupportedLanguage() {
        ReviewService service = new ReviewService(null);

        ReviewRequest request = new ReviewRequest(
                "System.out.println(\"Hello\");",
                "javascript"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.review(request)
        );
    }

    @Test
    void rejectsCodeThatIsTooLong() {
        ReviewService service = new ReviewService(null);

        String code = "a".repeat(10_001);

        ReviewRequest request = new ReviewRequest(code, "java");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.review(request)
        );
    }

    @Test
    void acceptsValidRequest() {
        AnalyzerClient analyzerClient = mock(AnalyzerClient.class);

        ReviewResponse response = new ReviewResponse(java.util.List.of());

        when(analyzerClient.analyze("System.out.println(\"Hello\");", "java"))
                .thenReturn(response);

        ReviewService service = new ReviewService(analyzerClient);

        ReviewRequest request = new ReviewRequest(
                "System.out.println(\"Hello\");",
                "java"
        );

        ReviewResponse result = service.review(request);

        assertSame(response, result);

        verify(analyzerClient).analyze(
                "System.out.println(\"Hello\");",
                "java"
        );
    }
}