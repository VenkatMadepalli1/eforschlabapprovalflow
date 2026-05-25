package com.eforsch.util;

import java.util.regex.Pattern;

public class EmailValidator {

    // Method to validate an email address
    public static boolean isValidEmail(String email) {
        // Regular expression for a valid email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,7}$";

        // Compile the regex
        Pattern pattern = Pattern.compile(emailRegex);

        // Check if the email is null
        if (email == null) {
            return false;
        }

        // Match the email with the regex
        return pattern.matcher(email).matches();
    }

    
}
