package card.application.onboarding.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EmploymentRequest implements VerificationRequest {
    private String emiratesId;
    private String fullName;
    private String employmentId;
}
