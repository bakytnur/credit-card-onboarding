package card.application.onboarding.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RiskEvaluationResponse {
    String emiratesId;
    int evaluationScore;
}
