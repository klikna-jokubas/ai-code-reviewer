    package com.jokubas.aicodereviewer.service;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClient;

    @Service
    public class AnalyzerClient {

        private final RestClient restClient;
        private final String analyzerUrl;

        public AnalyzerClient(
                RestClient restClient,
                @Value("${analyzer.url:http://localhost:8000}") String analyzerUrl) {
            this.restClient = restClient;
            this.analyzerUrl = analyzerUrl;
        }

        public ReviewResponse analyze(String code, String language) {
            CodeRequest request = new CodeRequest(
                    code,
                    language
            );

            return restClient.post()
                    .uri(analyzerUrl + "/analyze")
                    .body(request)
                    .retrieve()
                    .body(ReviewResponse.class);
        }
    }