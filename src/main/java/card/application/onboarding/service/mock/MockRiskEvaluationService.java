package card.application.onboarding.service.mock;

import card.application.onboarding.model.request.RiskEvaluationRequest;
import card.application.onboarding.model.response.RiskEvaluationResponse;
import card.application.onboarding.service.external.RiskEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.http.HttpClient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Service
public class MockRiskEvaluationService {

    private WireMockServer wireMockServer;
    private RiskEvaluationService riskEvaluationService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    void setUp() {
        // Start WireMock server on a random port
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();

        // Configure WireMock to use this instance
        WireMock.configureFor(wireMockServer.port());

        // Initialize your ExternalService with the WireMock base URL
        HttpClient httpClient = HttpClient.newHttpClient();
        // risk evaluation service
        riskEvaluationService = new RiskEvaluationService(httpClient, mapper);
    }

    @PreDestroy
    void tearDown() {
        // Stop the WireMock server after each test
        wireMockServer.stop();
    }

    @SneakyThrows
    public int evaluateRisk(RiskEvaluationRequest request) {
        var response = new RiskEvaluationResponse(request.getEmiratesId(), 100);
        String body = mapper.writeValueAsString(response);
        // Stub the endpoint with WireMock
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/risk/evaluation"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(body)
                        .withHeader("Content-Type", "application/json")));

        // Call the method that uses the HTTP client
        return riskEvaluationService.evaluateRisk(request);
    }
}
