package card.application.onboarding.service.mock;

import card.application.onboarding.model.request.ComplianceCheckRequest;
import card.application.onboarding.model.response.ComplianceCheckResponse;
import card.application.onboarding.service.external.ComplianceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;

@Service
public class MockComplianceService {

    @Autowired
    private WireMockServer wireMockServer;
    private ComplianceService externalService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    void setUp() {
        // Reset mappings before each test
        wireMockServer.resetAll();

        // Initialize your ExternalService with the WireMock base URL
        HttpClient httpClient = HttpClient.newHttpClient();
        // complianceService
        externalService = new ComplianceService(httpClient, mapper, "http://localhost:" + wireMockServer.port());
    }

    @SneakyThrows
    public boolean performComplianceCheck(ComplianceCheckRequest request) {
        var response = new ComplianceCheckResponse(request.getEmiratesId(), true);
        String body = mapper.writeValueAsString(response);
        // Stub the endpoint with WireMock
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/compliance"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(body)
                        .withHeader("Content-Type", "application/json")));

        // Call the method that uses the HTTP client
        return externalService.performComplianceCheck(request);
    }
}
