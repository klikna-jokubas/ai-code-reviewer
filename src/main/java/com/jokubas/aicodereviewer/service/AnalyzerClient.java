    package com.jokubas.aicodereviewer.service;

    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClient;

    @Service
    public class AnalyzerClient {

        private final RestClient restClient;

        public AnalyzerClient(RestClient restClient) {
            this.restClient = restClient;
        }

        public ReviewResponse analyze(String code, String language) {
            CodeRequest request = new CodeRequest(
                    code,
                    language
            );

            return restClient.post()
                    .uri("http://localhost:8000/analyze")
                    .body(request)
                    .retrieve()
                    .body(ReviewResponse.class);
        }
    }