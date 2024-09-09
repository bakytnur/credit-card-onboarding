package card.application.onboarding.service.external;

import card.application.onboarding.model.request.EmploymentRequest;
import card.application.onboarding.model.response.EmploymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmploymentVerificationService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public EmploymentVerificationService(HttpClient httpClient, ObjectMapper mapper, String baseUrl) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    @SneakyThrows
    public boolean verifyEmployment(EmploymentRequest employmentRequest) {
        // Serialize EcaRequest object to JSON string
        String requestBody = mapper.writeValueAsString(employmentRequest);

        // Create a POST request with JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/employment"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            return false;
        }

        if (response.statusCode() == HttpStatus.OK.value()) {
            var res = mapper.readValue(response.body(), EmploymentResponse.class);
            // TODO: store employment status
            return res.isEmployed();
        }

        return false;
    }
}
