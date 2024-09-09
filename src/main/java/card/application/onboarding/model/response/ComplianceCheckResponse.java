package card.application.onboarding.model.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ComplianceCheckResponse {
    String emiratesId;
    boolean compliancePassed;
}
