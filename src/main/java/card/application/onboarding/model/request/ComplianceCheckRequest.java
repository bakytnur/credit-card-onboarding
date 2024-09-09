package card.application.onboarding.model.request;

import lombok.Data;

@Data
public class ComplianceCheckRequest implements VerificationRequest{
    private String emiratesId;
    private String fullName;
    private String mobileNumber;
    private String nationality;
}
