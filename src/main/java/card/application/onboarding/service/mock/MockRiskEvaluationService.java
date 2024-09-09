package card.application.onboarding.service.mock;

import card.application.onboarding.model.request.RiskEvaluationRequest;
import card.application.onboarding.model.response.RiskEvaluationResponse;
import card.application.onboarding.service.external.RiskEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;

@Service
public class MockRiskEvaluationService {
    @Autowired
    private WireMockServer wireMockServer;
    private RiskEvaluationService riskEvaluationService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    void setUp() {
        // Reset mappings before each test
        wireMockServer.resetAll();

        // Initialize your ExternalService with the WireMock base URL
        HttpClient httpClient = HttpClient.newHttpClient();
        // risk evaluation service
        riskEvaluationService = new RiskEvaluationService(httpClient, mapper, "http://localhost:" + wireMockServer.port());
    }

    @SneakyThrows
    public int evaluateRisk(RiskEvaluationRequest request) {
        var response = new RiskEvaluationResponse(request.getEmiratesId(), 100);
        String body = mapper.writeValueAsString(response);
        // Stub the endpoint with WireMock
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/risk/evaluation"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(body)
                        .withHeader("Content-Type", "application/json")));

        // Call the method that uses the HTTP client
        return riskEvaluationService.evaluateRisk(request);
    }
}
