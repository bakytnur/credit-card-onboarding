package card.application.common.constants;

import card.application.common.exception.UnknownVerificationException;
import lombok.Getter;

@Getter
public enum VerificationStatus {
    IDENTITY_UNKNOWN(0),
    IDENTITY_VERIFIED(1),
    COMPLIANCE_CHECKED(2),
    EMPLOYMENT_VERIFIED(4),
    RISK_EVALUATED(8),
    BEHAVIORAL_ANALYSIS_CHECKED(16);

    public static VerificationStatus getStateByValue(int state) {
        return switch (state) {
            case 0 -> IDENTITY_UNKNOWN;
            case 1 -> IDENTITY_VERIFIED;
            case 2 -> COMPLIANCE_CHECKED;
            case 4 -> EMPLOYMENT_VERIFIED;
            case 8 -> RISK_EVALUATED;
            case 16 -> BEHAVIORAL_ANALYSIS_CHECKED;
            default -> throw new UnknownVerificationException("unknown status");
        };
    }

    private final int state;

    VerificationStatus(int state) {
        this.state = state;
    }
}
