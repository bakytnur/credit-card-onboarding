package card.application.onboarding.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EmploymentResponse {
    String emiratesId;
    String employmentId;
    boolean isEmployed;
    String employmentDate;
}
