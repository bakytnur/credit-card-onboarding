package card.application.common;

public class EmiratesIdValidator {

    // Method to validate Emirates ID
    public static boolean isValidEmiratesID(String emiratesID) {
        // Check if the ID is null or not 15 characters long
        if (emiratesID == null || emiratesID.length() != 15) {
            return false;
        }

        // Check if all characters are digits
        for (int i = 0; i < emiratesID.length(); i++) {
            if (!Character.isDigit(emiratesID.charAt(i))) {
                return false;
            }
        }

        // Emirates ID is valid
        return true;
    }
}
