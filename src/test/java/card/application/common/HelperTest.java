package card.application.common;

import card.application.common.constants.VerificationStatus;
import card.application.onboarding.entity.CardUser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelperTest {
    @ParameterizedTest
    @MethodSource("provideUserStatusAndExpectedResult")
    void testIsUserVerifiedForStatus(CardUser user, VerificationStatus status, boolean expectedResult) {
        boolean result = Helper.isUserVerifiedForStatus(user, status);
        assertEquals(expectedResult, result);
    }

    // MethodSource provides the arguments for the test
    private static Stream<Arguments> provideUserStatusAndExpectedResult() {
        return Stream.of(
                Arguments.of(new CardUser(1), VerificationStatus.IDENTITY_VERIFIED, true),
                Arguments.of(new CardUser(2), VerificationStatus.EMPLOYMENT_VERIFIED, false),
                Arguments.of(new CardUser(7), VerificationStatus.COMPLIANCE_CHECKED, true),
                Arguments.of(new CardUser(15), VerificationStatus.RISK_EVALUATED, true),
                Arguments.of(new CardUser(3), VerificationStatus.BEHAVIORAL_ANALYSIS_CHECKED, false)
        );
    }
}
