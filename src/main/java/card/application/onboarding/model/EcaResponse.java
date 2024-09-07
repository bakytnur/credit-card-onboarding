package card.application.onboarding.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class EcaResponse {
    private boolean isValid;
    private String expiryDate;
}
