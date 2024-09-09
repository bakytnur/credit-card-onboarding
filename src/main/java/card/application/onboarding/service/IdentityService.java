package card.application.onboarding.service;

import card.application.common.Helper;
import card.application.common.constants.VerificationStatus;
import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.request.VerificationRequest;
import card.application.onboarding.model.response.EcaResponse;
import card.application.onboarding.repository.IdentityRepository;
import card.application.onboarding.service.mock.MockEcaService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static card.application.common.Helper.isUserVerifiedForStatus;
import static card.application.common.constants.Constants.IDENTITY_VERIFICATION_SCORE;

@Service
public class IdentityService {
    private final MockEcaService mockEcaService;
    private final IdentityRepository identityRepository;

    public IdentityService(MockEcaService mockEcaService, IdentityRepository identityRepository) {
        this.mockEcaService = mockEcaService;
        this.identityRepository = identityRepository;
    }

    public VerificationStatus verifyIdentity(@NotNull VerificationRequest request) {
        // Validation
        Helper.validateRequest(request);

        try {
            // check from the DB for an existing user
            var existingUser = getExistingIdentityForUser(request);
            if (existingUser != null) return VerificationStatus.IDENTITY_VERIFIED;

            // if not found in DB or with expired EID, call ECA API
            EcaResponse response = mockEcaService.getMockUserIdentity(request.getEmiratesId(), request.getFullName());
            VerificationStatus status = response.isValid()
                    ? VerificationStatus.IDENTITY_VERIFIED : VerificationStatus.IDENTITY_UNKNOWN;
            // store the ECA response
            CardUser user = buildCardUser(request.getEmiratesId(), request.getFullName(),
                    status, response.getExpiryDate());

            saveUserIdentity(user);
            return status;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    public void saveUserIdentity(CardUser user) {
        identityRepository.save(user);
    }

    public CardUser getExistingIdentityForUser(@NotNull VerificationRequest request) {
        // check if existing user
        Optional<CardUser> existingUser = identityRepository.findByEmiratesId(request.getEmiratesId());
        if (existingUser.isEmpty()) {
            return null;
        }

        CardUser user = existingUser.get();
        if (isUserVerifiedForStatus(user, VerificationStatus.IDENTITY_VERIFIED)
                // check EID validity
                && user.getExpiryDate().isAfter(LocalDate.now())) {
            return user;
        }
        return null;
    }

    private CardUser buildCardUser(String emiratesId, String fullName, VerificationStatus status, String expiryDate) {
        CardUser cardUser = new CardUser();
        cardUser.setName(fullName);
        cardUser.setEmiratesId(emiratesId);
        cardUser.setStatus(status.getState());
        cardUser.setScore(IDENTITY_VERIFICATION_SCORE);
        cardUser.setExpiryDate(StringUtils.hasText(expiryDate) ? LocalDate.parse(expiryDate) : null);

        return cardUser;
    }
}
