
package com.eforsch.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eforsch.ApiResponse;
import com.eforsch.entity.UserDetails;
import com.eforsch.repository.UserDetailsRepository;
import com.eforsch.util.EmailUtil;
import com.eforsch.util.OtpUtil;
@Service
public class ForgotPasswordService {

    private static final int OTP_EXPIRY_MINUTES = 10;

    @Autowired
    private UserDetailsRepository userDetailsRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Inject here

    @Autowired
    private EmailUtil emailUtil;

    public ApiResponse handleForgotPassword(String email) {
        issueOtp(email);
        return new ApiResponse(200, "SUCCESS", "If the email is registered, an OTP has been sent.");
    }

    public ApiResponse resendOtp(String email) {
        issueOtp(email);
        return new ApiResponse(200, "SUCCESS", "If the email is registered, a new OTP has been sent.");
    }

    private void issueOtp(String email) {
        UserDetails user = userDetailsRepository.findByEmailWithRole(email);
        if (user != null) {
            String otp = OtpUtil.generateOtp();
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            user.setOtpVerifiedAt(null);
            userDetailsRepository.save(user);
            emailUtil.sendOtpEmail(email, otp);
        }
    }

    public ApiResponse validateOtp(String email, String otp) {
        UserDetails user = userDetailsRepository.findByEmailWithRole(email);
        if (user == null || user.getOtp() == null) {
            return new ApiResponse(400, "FAILED", "Invalid or expired OTP.");
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return new ApiResponse(400, "FAILED", "OTP has expired.");
        }
        if (!otp.equals(user.getOtp())) {
            return new ApiResponse(400, "FAILED", "Invalid OTP.");
        }
        // Clear OTP after successful validation
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpVerifiedAt(LocalDateTime.now());
        userDetailsRepository.save(user);
        return new ApiResponse(200, "SUCCESS", "OTP validated successfully.");
    }

    public ApiResponse resetPassword(String email, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            return new ApiResponse(400, "FAILED", "Passwords do not match.");
        }
        UserDetails user = userDetailsRepository.findByEmailWithRole(email);
        if (user == null) {
            return new ApiResponse(400, "FAILED", "User not found.");
        }
        // Ensure OTP was validated within the last 5 minutes
        LocalDateTime verifiedAt = user.getOtpVerifiedAt();
        if (verifiedAt == null) {
            return new ApiResponse(400, "FAILED", "OTP verification required before resetting password.");
        }
        if (verifiedAt.isBefore(LocalDateTime.now().minusMinutes(5))) {
            return new ApiResponse(400, "FAILED", "Reset password time over. Please request a new OTP.");
        }
        user.setPassword(passwordEncoder.encode(newPassword)); // ✅ Encoded
        // Clear the otpVerifiedAt after successful reset
        user.setOtpVerifiedAt(null);
        userDetailsRepository.save(user);
        return new ApiResponse(200, "SUCCESS", "Password reset successfully.");
    }
}