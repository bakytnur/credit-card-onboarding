package card.application.common;

public class FullNameValidator {

    // Method to validate the full name
    public static boolean isValidFullName(String fullName) {
        // Check if the name is null or empty
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        // Check if the name length is within a reasonable range (e.g., 1 to 100 characters)
        if (fullName.isEmpty() || fullName.length() > 100) {
            return false;
        }

        // Regular expression to match a valid full name (allowing letters, spaces, hyphens, and apostrophes)
        String nameRegex = "^[a-zA-Z\\s\\-\\']+$";

        // Validate the full name against the regex pattern
        return fullName.matches(nameRegex);
    }
}