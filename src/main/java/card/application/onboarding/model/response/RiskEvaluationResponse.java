package card.application.onboarding.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiskEvaluationResponse {
    String emiratesId;
    double evaluationScore;
}
