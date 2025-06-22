package org.example.util;
import org.example.model.UserInfoDto;
import java.util.regex.Pattern;
public class ValidationUtil {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    public static void validateUserAttributes(UserInfoDto userInfoDto) {
        if (userInfoDto.getUsername() == null || userInfoDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        if (userInfoDto.getPassword() == null || !Pattern.matches(PASSWORD_PATTERN, userInfoDto.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, contain a digit, a lowercase letter, an uppercase letter, and a special character.");
        }
    }
}

