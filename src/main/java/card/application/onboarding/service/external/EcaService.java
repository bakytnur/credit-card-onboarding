package card.application.onboarding.service.external;

import card.application.onboarding.model.request.EcaRequest;
import card.application.onboarding.model.response.EcaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EcaService {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public EcaService(HttpClient httpClient, ObjectMapper mapper, String baseUrl) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    public EcaResponse verifyUserIdentity(EcaRequest ecaRequest) throws IOException, InterruptedException {
        // Serialize EcaRequest object to JSON string
        String requestBody = mapper.writeValueAsString(ecaRequest);

        // Create a POST request with JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/data"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), EcaResponse.class);
        } else {
            throw new RuntimeException("Failed to fetch data from ECA service");
        }
    }
}