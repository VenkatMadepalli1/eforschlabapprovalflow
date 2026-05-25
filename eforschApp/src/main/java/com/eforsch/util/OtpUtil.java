
package com.eforsch.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int OTP_BOUND = 1_000_000;

    public static String generateOtp() {
        return String.format("%06d", RANDOM.nextInt(OTP_BOUND));
    }
}
