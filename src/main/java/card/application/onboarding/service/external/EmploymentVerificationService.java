package card.application.onboarding.service.external;

import card.application.onboarding.model.request.EcaRequest;
import card.application.onboarding.model.request.EmploymentRequest;
import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.response.EcaResponse;
import card.application.onboarding.model.response.EmploymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmploymentVerificationService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    @Value("{employment.base.url}")
    private String baseUrl;

    public EmploymentVerificationService(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @SneakyThrows
    public boolean verifyEmployment(EmploymentRequest employmentRequest) {
        // Serialize EcaRequest object to JSON string
        String requestBody = mapper.writeValueAsString(employmentRequest);

        // Create a POST request with JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/data"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            return false;
        }

        if (response.statusCode() == 200) {
            EmploymentResponse res = mapper.readValue(response.body(), EmploymentResponse.class);
            // TODO: store employment status
            return res.isEmployed();
        }

        return false;
    }
}
