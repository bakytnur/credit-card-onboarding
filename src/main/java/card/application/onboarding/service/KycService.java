package card.application.onboarding.service;

import card.application.common.EmiratesIdValidator;
import card.onboarding.model.IdentityVerificationStatus;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.jetbrains.annotations.NotNull;

public class KycService {
    public IdentityVerificationStatus verifyIdentity(@NotNull IdentityVerificationRequest request) {
        // Validation
        boolean validEmiratesId = EmiratesIdValidator.isValidEmiratesID(request.getEmiratesId());

        return IdentityVerificationStatus.IDENTITY_VERIFIED;
    }
}
