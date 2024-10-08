package card.application.onboarding.service.external;

import card.application.onboarding.model.request.ComplianceCheckRequest;
import card.application.onboarding.model.response.ComplianceCheckResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ComplianceService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public ComplianceService(HttpClient httpClient, ObjectMapper mapper, String baseUrl) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    @SneakyThrows
    public boolean performComplianceCheck(ComplianceCheckRequest complianceCheckRequest) {
        // Serialize EcaRequest object to JSON string
        String requestBody = mapper.writeValueAsString(complianceCheckRequest);

        // Create a POST request with JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/compliance"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            return false;
        }

        if (response.statusCode() == HttpStatus.OK.value()) {
            var res = mapper.readValue(response.body(), ComplianceCheckResponse.class);
            // TODO: store compliance status
            return res.isCompliancePassed();
        }

        return false;
    }
}
