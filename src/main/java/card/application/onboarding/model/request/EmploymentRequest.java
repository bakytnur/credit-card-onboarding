package card.application.onboarding.model.request;

import lombok.Data;

@Data
public class EmploymentRequest implements VerificationRequest {
    private String emiratesId;
    private String fullName;
    private String employmentId;
}
