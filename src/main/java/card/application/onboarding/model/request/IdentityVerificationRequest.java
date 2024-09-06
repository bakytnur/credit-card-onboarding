package card.onboarding.model.request;

import lombok.Data;

@Data
public class IdentityVerificationRequest {
    private String emiratesId;
    private String fullName;
}
