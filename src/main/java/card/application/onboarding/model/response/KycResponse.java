package card.application.onboarding.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class KycResponse {
    String emiratesId;
    int totalScore;
    // combination of the verifications completed
    int status;
}
