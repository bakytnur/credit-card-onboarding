package card.application.common.constants;

import card.application.common.exception.UnknownIdentityVerificationException;

public enum IdentityVerificationStatus {
    IDENTITY_VERIFIED(0),
    IDENTITY_UNKNOWN(1),
    EXISTING_USER(2),
    EXTRA_VERIFICATION_NEEDED(3);

    public static IdentityVerificationStatus getStateByValue(int state) {
        return switch (state) {
            case 0 -> IDENTITY_VERIFIED;
            case 1 -> IDENTITY_UNKNOWN;
            case 2 -> EXISTING_USER;
            case 3 -> EXTRA_VERIFICATION_NEEDED;
            default -> throw new UnknownIdentityVerificationException("unknown status");
        };
    }

    public int getState() {
        return state;
    }

    private final int state;

    IdentityVerificationStatus(int state) {
        this.state = state;
    }
}
