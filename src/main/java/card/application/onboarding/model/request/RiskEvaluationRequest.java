package card.application.onboarding.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RiskEvaluationRequest implements VerificationRequest {
    String emiratesId;
    String fullName;
}
