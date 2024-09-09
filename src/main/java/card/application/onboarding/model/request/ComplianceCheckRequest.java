package card.application.onboarding.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ComplianceCheckRequest implements VerificationRequest{
    private String emiratesId;
    private String fullName;
    private String mobileNumber;
    private String nationality;
}
