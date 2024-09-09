package card.application.onboarding.service.external;

import card.application.onboarding.model.request.EcaRequest;
import card.application.onboarding.model.response.EcaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EcaService {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    @Value("{eca.base.url}")
    private String baseUrl;

    public EcaService(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @SneakyThrows
    public EcaResponse verifyUserIdentity(EcaRequest ecaRequest) {
        // Serialize EcaRequest object to JSON string
        String requestBody = mapper.writeValueAsString(ecaRequest);

        // Create a POST request with JSON body
        var request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/data"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            return null;
        }

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), EcaResponse.class);
        }

        return null;
    }
}