package card.application.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmiratesIdValidatorTest {

    @ParameterizedTest
    @MethodSource("validEmiratesIDProvider")
    void testValidEmiratesIDs(String emiratesID) {
        assertTrue(EmiratesIdValidator.isValidEmiratesID(emiratesID), "The Emirates ID '" + emiratesID + "' should be valid.");
    }

    @ParameterizedTest
    @MethodSource("invalidEmiratesIDProvider")
    void testInvalidEmiratesIDs(String emiratesID) {
        assertFalse(EmiratesIdValidator.isValidEmiratesID(emiratesID), "The Emirates ID '" + emiratesID + "' should be invalid.");
    }

    // Provides valid Emirates IDs for testing
    static Stream<String> validEmiratesIDProvider() {
        return Stream.of(
                "784199123456789",     // Valid Emirates ID with 15 digits
                "123456789012345",     // Another valid Emirates ID with 15 digits
                "000000000000000"      // Valid Emirates ID with all zeros (edge case)
        );
    }

    // Provides invalid Emirates IDs for testing
    static Stream<String> invalidEmiratesIDProvider() {
        return Stream.of(
                "78419912345678",      // Invalid Emirates ID with less than 15 digits
                "78419912345abcd",     // Invalid Emirates ID with letters
                "78419912345@#$%^",    // Invalid Emirates ID with special characters
                "",                    // Empty string
                "   ",                 // String with spaces only
                null                   // Null value
        );
    }
}