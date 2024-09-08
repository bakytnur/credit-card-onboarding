package card.application.common;

import card.application.common.constants.VerificationStatus;
import card.application.onboarding.entity.CardUser;

public class Helper {
    public static boolean isUserVerifiedForStatus(CardUser user, VerificationStatus status) {
        return (user.getStatus() & status.getState()) == status.getState();
    }
}
