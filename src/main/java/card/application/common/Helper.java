package card.application.common;

import card.application.common.constants.VerificationStatus;
import card.application.common.exception.InputValidationException;
import card.application.onboarding.entity.CardUser;
import card.application.onboarding.model.request.KycRequest;
import card.application.onboarding.model.request.VerificationRequest;

public class Helper {
    public static boolean isUserVerifiedForStatus(CardUser user, VerificationStatus status) {
        return (user.getStatus() & status.getState()) != 0;
    }

    public static void validateRequest(VerificationRequest request) {
        boolean validEmiratesId = EmiratesIdValidator.isValidEmiratesID(request.getEmiratesId());
        if (!validEmiratesId) {
            throw new InputValidationException("Invalid Emirates Id");
        }

        boolean validFullName = FullNameValidator.isValidFullName(request.getFullName());
        if (!validFullName) {
            throw new InputValidationException("Full name is invalid");
        }

        if (request instanceof KycRequest kycRequest) {
            boolean validMobileNo = MobileNumberValidator.isValidUaeMobileNumber(kycRequest.getMobileNumber());
            if (!validMobileNo) {
                throw new InputValidationException("Mobile number is invalid");
            }

            if (kycRequest.getEmployerId() == null) {
                throw new InputValidationException("Employer Id is invalid");
            }
        }
    }
}
