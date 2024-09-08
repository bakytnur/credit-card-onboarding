package card.application.onboarding.service;

import card.application.common.EmiratesIdValidator;
import card.application.common.FullNameValidator;
import card.application.common.constants.IdentityVerificationStatus;
import card.application.common.exception.InputValidationException;
import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.EcaResponse;
import card.application.onboarding.repository.KycRepository;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;

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
            EcaResponse response = mockEcaService.getMockUserIdentity(request.getEmiratesId(), request.getFullName());
            IdentityVerificationStatus status;
            if (response.isValid()) {
                status = IdentityVerificationStatus.IDENTITY_VERIFIED;
            } else {
                status = IdentityVerificationStatus.IDENTITY_UNKNOWN;
            }

            CardUser saveResponse = kycRepository.save(buildCardUser(request.getEmiratesId(), request.getFullName(),
                    status, response.getExpiryDate()));

            return status;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private CardUser buildCardUser(String emiratesId, String fullName, IdentityVerificationStatus status, String expiryDate) {
        return CardUser.builder()
                .name(fullName)
                .emiratesId(emiratesId)
                .status(status.getState())
                .expiryDate(StringUtils.hasText(expiryDate) ? LocalDate.parse(expiryDate) : null)
                .build();
    }
}
