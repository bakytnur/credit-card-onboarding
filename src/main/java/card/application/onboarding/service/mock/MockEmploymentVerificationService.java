package card.application.onboarding.service.mock;

import card.application.onboarding.model.request.EmploymentRequest;
import card.application.onboarding.model.response.EmploymentResponse;
import card.application.onboarding.service.external.EmploymentVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;

@Service
public class MockEmploymentVerificationService {
    @Autowired
    private WireMockServer wireMockServer;
    private EmploymentVerificationService employmentVerificationService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    void setUp() {
        // Reset mappings before each test
        wireMockServer.resetAll();

        // Initialize your ExternalService with the WireMock base URL
        HttpClient httpClient = HttpClient.newHttpClient();
        // employment service
        employmentVerificationService = new EmploymentVerificationService(httpClient, mapper, "http://localhost:" + wireMockServer.port());
    }

    @SneakyThrows
    public boolean verifyEmployment(EmploymentRequest request) {
        var response = new EmploymentResponse(request.getEmiratesId(), request.getEmploymentId(),
                true, "2021-11-06");
        String body = mapper.writeValueAsString(response);
        // Stub the endpoint with WireMock
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/employment"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody(body)
                        .withHeader("Content-Type", "application/json")));

        // Call the method that uses the HTTP client
        return employmentVerificationService.verifyEmployment(request);
    }
}
