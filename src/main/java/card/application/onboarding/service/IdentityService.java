package card.application.onboarding.service;

import card.application.common.EmiratesIdValidator;
import card.application.common.FullNameValidator;
import card.application.common.constants.VerificationStatus;
import card.application.common.exception.InputValidationException;
import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.EcaResponse;
import card.application.onboarding.repository.IdentityRepository;
import card.onboarding.model.request.IdentityVerificationRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static card.application.common.Helper.isUserVerifiedForStatus;

@Service
public class IdentityService {
    private final MockEcaService mockEcaService;
    private final IdentityRepository identityRepository;

    public IdentityService(MockEcaService mockEcaService, IdentityRepository identityRepository) {
        this.mockEcaService = mockEcaService;
        this.identityRepository = identityRepository;
    }

    public VerificationStatus verifyIdentity(@NotNull IdentityVerificationRequest request) {
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
            // check the DB for an existing user
            boolean isExistingUser = checkExistingUser(request);
            if (isExistingUser) return VerificationStatus.IDENTITY_VERIFIED;

            // if not found in DB or with expired EID, call ECA API
            EcaResponse response = mockEcaService.getMockUserIdentity(request.getEmiratesId(), request.getFullName());
            VerificationStatus status = response.isValid()
                    ? VerificationStatus.IDENTITY_VERIFIED : VerificationStatus.IDENTITY_UNKNOWN;
            // store the ECA response
            CardUser user = buildCardUser(request.getEmiratesId(), request.getFullName(),
                    status, response.getExpiryDate());
            identityRepository.save(user);

            return status;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    private boolean checkExistingUser(@NotNull IdentityVerificationRequest request) {
        // check if existing user
        Optional<CardUser> existingUser = identityRepository.findByEmiratesId(request.getEmiratesId());
        if (existingUser.isPresent()) {
            CardUser user = existingUser.get();
            return isUserVerifiedForStatus(user, VerificationStatus.IDENTITY_VERIFIED)
                    // check EID validity
                    && user.getExpiryDate().isAfter(LocalDate.now());
        }
        return false;
    }

    private CardUser buildCardUser(String emiratesId, String fullName, VerificationStatus status, String expiryDate) {
        CardUser cardUser = new CardUser();
        cardUser.setName(fullName);
        cardUser.setEmiratesId(emiratesId);
        cardUser.setStatus(status.getState());
        cardUser.setExpiryDate(StringUtils.hasText(expiryDate) ? LocalDate.parse(expiryDate) : null);

        return cardUser;
    }
}
