package card.application.onboarding.service.external;

import card.application.onboarding.model.request.RiskEvaluationRequest;
import card.application.onboarding.model.response.RiskEvaluationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RiskEvaluationService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public RiskEvaluationService(HttpClient httpClient, ObjectMapper mapper, String baseUrl) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    @SneakyThrows
    public int evaluateRisk(RiskEvaluationRequest riskEvaluationRequest) {
        // Serialize EcaRequest object to JSON string
        String requestBody = mapper.writeValueAsString(riskEvaluationRequest);

        // Create a POST request with JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/risk/evaluation"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            return 0;
        }

        if (response.statusCode() == HttpStatus.OK.value()) {
            var res = mapper.readValue(response.body(), RiskEvaluationResponse.class);
            return res.getEvaluationScore();
        }

        return 0;
    }
}
