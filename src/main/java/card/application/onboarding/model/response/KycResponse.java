package card.application.onboarding.model.response;

import lombok.Data;

@Data
public class KycResponse {
    String emiratesId;
    int totalScore;
    // combination of the verifications completed
    int status;
}
