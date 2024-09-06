package card.application.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FullNameValidatorTest {

    @ParameterizedTest
    @MethodSource("validFullNamesProvider")
    void testValidFullNames(String fullName) {
        assertTrue(FullNameValidator.isValidFullName(fullName), "The name '" + fullName + "' should be valid.");
    }

    @ParameterizedTest
    @MethodSource("invalidFullNamesProvider")
    void testInvalidFullNames(String fullName) {
        assertFalse(FullNameValidator.isValidFullName(fullName), "The name '" + fullName + "' should be invalid.");
    }

    // Provides valid full names for testing
    static Stream<String> validFullNamesProvider() {
        return Stream.of(
                "John Doe",                    // Valid full name
                "Anne-Marie O'Neill",          // Valid full name with hyphen and apostrophe
                "Madonna",                     // Valid single name
                "a".repeat(100)                // Valid full name with maximum length (100 characters)
        );
    }

    // Provides invalid full names for testing
    static Stream<String> invalidFullNamesProvider() {
        return Stream.of(
                "John123",                     // Invalid name with numbers
                "John@Doe",                    // Invalid name with special characters
                "",                            // Empty string
                "   ",                         // Name with only spaces
                "a".repeat(101),               // Name longer than 100 characters
                null                           // Null value
        );
    }
}