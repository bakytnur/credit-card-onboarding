package card.application.onboarding.model.request;

import lombok.Data;

@Data
public class IdentityVerificationRequest implements VerificationRequest {
    private String emiratesId;
    private String fullName;
}
