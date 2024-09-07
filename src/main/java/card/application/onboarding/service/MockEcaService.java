package card.application.onboarding.service;

import card.application.onboarding.model.EcaRequest;
import card.application.onboarding.model.EcaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.http.HttpClient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Service
public class MockEcaService {

    private WireMockServer wireMockServer;
    private EcaService externalService;
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
        // eca
        externalService = new EcaService(httpClient, mapper, "http://localhost:" + wireMockServer.port());
    }

    @PreDestroy
    void tearDown() {
        // Stop the WireMock server after each test
        wireMockServer.stop();
    }

    public EcaResponse testGetExternalData(String emiratesId, String fullName) throws IOException, InterruptedException {
        EcaRequest ecaRequest = new EcaRequest(emiratesId, fullName);
        EcaResponse ecaResponse = new EcaResponse(true, "2025/11/01");
        String body = mapper.writeValueAsString(ecaResponse);
        // Stub the endpoint with WireMock
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/data"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(body)
                        .withHeader("Content-Type", "application/json")));

        // Call the method that uses the HTTP client
        return externalService.verifyUserIdentity(ecaRequest);
    }
}
