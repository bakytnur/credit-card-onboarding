package card.application.onboarding.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KycRequest implements VerificationRequest {
    private String emiratesId;
    private String fullName;

    private String mobileNumber;
    private String employerId;
    private String nationality;
}
