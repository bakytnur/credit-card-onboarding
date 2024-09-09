package card.application.common;

import java.util.regex.Pattern;

public class MobileNumberValidator {
    // Regex pattern for UAE mobile numbers
    private static final Pattern UAE_MOBILE_NUMBER_PATTERN = Pattern.compile("^(\\+971|0)(50|52|55|56|58)\\d{7}$");

    public static boolean isValidUaeMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            return false;
        }
        return UAE_MOBILE_NUMBER_PATTERN.matcher(mobileNumber).matches();
    }
}
