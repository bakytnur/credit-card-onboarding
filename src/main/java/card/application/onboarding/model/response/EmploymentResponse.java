package card.application.onboarding.model.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmploymentResponse {
    String emiratesId;
    String employmentId;
    boolean isEmployed;
    LocalDate employmentDate;
}
