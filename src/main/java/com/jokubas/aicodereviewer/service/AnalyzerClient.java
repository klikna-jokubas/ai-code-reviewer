    package com.jokubas.aicodereviewer.service;

    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestClient;

    @Service
    public class AnalyzerClient {

        private final RestClient restClient;

        public AnalyzerClient(RestClient restClient) {
            this.restClient = restClient;
        }

        public String analyze(String code) {
            CodeRequest request = new CodeRequest(code);

            return restClient.post()
                    .uri("http://localhost:8000/analyze")
                    .body(request)
                    .retrieve()
                    .body(String.class);
        }
    }