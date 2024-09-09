package card.application.onboarding.service.mock;

import card.application.onboarding.model.request.EcaRequest;
import card.application.onboarding.model.response.EcaResponse;
import card.application.onboarding.service.external.EcaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;

@Service
public class MockEcaService {
    @Autowired
    private WireMockServer wireMockServer;
    private EcaService externalService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    void setUp() {
        // Reset mappings before each test
        wireMockServer.resetAll();

        // Initialize your ExternalService with the WireMock base URL
        HttpClient httpClient = HttpClient.newHttpClient();
        // eca
        externalService = new EcaService(httpClient, mapper, "http://localhost:" + wireMockServer.port());
    }

    @SneakyThrows
    public EcaResponse verifyUserIdentity(EcaRequest request) {
        var ecaResponse = new EcaResponse(true, "2025-11-01");
        String body = mapper.writeValueAsString(ecaResponse);
        // Stub the endpoint with WireMock
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/eca"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(body)
                        .withHeader("Content-Type", "application/json")));

        // Call the method that uses the HTTP client
        return externalService.verifyUserIdentity(request);
    }
}
