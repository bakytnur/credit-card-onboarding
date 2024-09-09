package card.application.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MobileNumberValidatorTest {
    @ParameterizedTest
    @CsvSource({
            "0501234567, true",      // Valid UAE number starting with 050
            "0559876543, true",      // Valid UAE number starting with 055
            "+971561234567, true",   // Valid UAE number with +971 country code
            "0587654321, true",      // Valid UAE number starting with 058
            "0591234567, false",     // Invalid UAE number starting with 059
            "1234567890, false",     // Invalid UAE number
            "+97150123456789, false" // Invalid UAE number with extra digits
    })
    void testIsValidUaeMobileNumber(String mobileNumber, boolean expectedResult) {
        boolean isValid = MobileNumberValidator.isValidUaeMobileNumber(mobileNumber);
        assertEquals(expectedResult, isValid);
    }
}
