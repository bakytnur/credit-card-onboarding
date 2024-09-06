package card.application.onboarding.service;

import card.application.common.EmiratesIdValidator;
import card.application.common.FullNameValidator;
import card.application.common.exception.InputValidationException;
import card.application.common.constants.IdentityVerificationStatus;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.jetbrains.annotations.NotNull;

public class KycService {
    public IdentityVerificationStatus verifyIdentity(@NotNull IdentityVerificationRequest request) {
        // Validation
        boolean validEmiratesId = EmiratesIdValidator.isValidEmiratesID(request.getEmiratesId());
        if (!validEmiratesId) {
            throw new InputValidationException("Invalid Emirates Id");
        }

        boolean validFullName = FullNameValidator.isValidFullName(request.getFullName());
        if (!validFullName) {
            throw new InputValidationException("Full name is invalid");
        }
        
        return IdentityVerificationStatus.IDENTITY_VERIFIED;
    }
}
