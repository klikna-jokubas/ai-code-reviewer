    package com.jokubas.aicodereviewer.service;

    import org.junit.jupiter.api.Test;
    import org.springframework.http.HttpMethod;
    import org.springframework.test.web.client.MockRestServiceServer;
    import org.springframework.web.client.RestClient;
    import org.springframework.http.MediaType;

    import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
    import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
    import static org.junit.jupiter.api.Assertions.*;

    class AnalyzerClientTest {

        @Test
        void analyzeSendsRequestAndReturnsResponse() {

            RestClient.Builder builder = RestClient.builder()
                    .baseUrl("http://localhost:8000");

            MockRestServiceServer server =
                    MockRestServiceServer.bindTo(builder).build();

            RestClient restClient = builder.build();

            AnalyzerClient analyzerClient = new AnalyzerClient(restClient);

            server.expect(requestTo("http://localhost:8000/analyze"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(content().json("""
                    {
                        "code": "System.out.println(123);",
                        "language": "java"
                    }
                    """))
                    .andRespond(withSuccess("""
                    {
                        "issues": [
                            {
                                "severity": "HIGH",
                                "type": "HARDCODED_SECRET",
                                "line": 1,
                                "message": "Possible hardcoded secret detected.",
                                "suggestion": "Use an environment variable instead."
                            }
                        ]
                    }
                    """, MediaType.APPLICATION_JSON));

            ReviewResponse result = analyzerClient.analyze(
                    "System.out.println(123);",
                    "java"
            );

            assertNotNull(result);
            assertNotNull(result.issues());
            assertEquals(1, result.issues().size());

            Issue issue = result.issues().get(0);

            assertEquals("HIGH", issue.severity());
            assertEquals("HARDCODED_SECRET", issue.type());
            assertEquals(1, issue.line());
            assertEquals("Possible hardcoded secret detected.", issue.message());
            assertEquals("Use an environment variable instead.", issue.suggestion());

            server.verify();
        }


    }