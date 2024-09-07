package card.application.onboarding.service;

import card.application.common.EmiratesIdValidator;
import card.application.common.FullNameValidator;
import card.application.common.constants.IdentityVerificationStatus;
import card.application.common.exception.InputValidationException;
import card.application.onboarding.model.EcaResponse;
import card.application.onboarding.repository.KycRepository;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KycService {
    private final MockEcaService mockEcaService;
    private final KycRepository kycRepository;

    public KycService(MockEcaService mockEcaService, KycRepository kycRepository) {
        this.mockEcaService = mockEcaService;
        this.kycRepository = kycRepository;
    }

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
        try {
            EcaResponse response = mockEcaService.testGetExternalData(request.getEmiratesId(), request.getFullName());
            kycRepository.storeKycResponse(request.getEmiratesId(), request.getFullName(), response.isValid(), response.getExpiryDate());

            if (response.isValid()) {
                return IdentityVerificationStatus.IDENTITY_VERIFIED;
            }

            return IdentityVerificationStatus.IDENTITY_UNKNOWN;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
