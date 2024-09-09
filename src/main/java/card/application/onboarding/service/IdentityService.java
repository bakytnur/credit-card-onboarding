package card.application.onboarding.service;

import card.application.common.Helper;
import card.application.common.constants.VerificationStatus;
import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.request.EcaRequest;
import card.application.onboarding.model.request.VerificationRequest;
import card.application.onboarding.repository.IdentityRepository;
import card.application.onboarding.service.mock.MockEcaService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        // check from the DB for an existing user
        var existingUser = getExistingIdentityForUser(request);
        if (existingUser != null) return VerificationStatus.IDENTITY_VERIFIED;

        // if not found in DB or with expired EID, call ECA API
        var response = mockEcaService.verifyUserIdentity(new EcaRequest(request.getEmiratesId(), request.getFullName()));
        var status = response.isValid()
                ? VerificationStatus.IDENTITY_VERIFIED : VerificationStatus.IDENTITY_UNKNOWN;
        // store the ECA response
        var user = buildCardUser(request.getEmiratesId(), request.getFullName(),
                status, response.getExpiryDate());

        saveUserIdentity(user);
        return status;
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

        var user = existingUser.get();
        if (isUserVerifiedForStatus(user, VerificationStatus.IDENTITY_VERIFIED)
                // check EID validity
                && user.getExpiryDate().isAfter(LocalDate.now())) {
            return user;
        }
        return null;
    }

    private CardUser buildCardUser(String emiratesId, String fullName, VerificationStatus status, String expiryDate) {
        var cardUser = new CardUser();
        cardUser.setName(fullName);
        cardUser.setEmiratesId(emiratesId);
        cardUser.setStatus(status.getState());
        cardUser.setScore(IDENTITY_VERIFICATION_SCORE);
        cardUser.setExpiryDate(StringUtils.hasText(expiryDate) ? LocalDate.parse(expiryDate) : null);

        return cardUser;
    }
}
