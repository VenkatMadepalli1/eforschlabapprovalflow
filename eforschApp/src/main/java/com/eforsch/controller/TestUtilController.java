package com.eforsch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.entity.UserDetails;
import com.eforsch.repository.UserDetailsRepository;

/**
 * ⚠️  TEST UTILITY — FOR INTERNAL/DEV USE ONLY
 *     DELETE THIS FILE BEFORE DEPLOYING TO PRODUCTION
 */
@RestController
@RequestMapping("/api/test")
public class TestUtilController {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Resets the password for any user by email.
     *
     * Usage:
     *   GET /api/test/resetPassword?email=lab@example.com&newPassword=Test@123
     *
     * After calling this, log in with:
     *   email    → lab@example.com
     *   password → Test@123
     *
     * ⚠️  DELETE before production
     */
    @GetMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {

        UserDetails user = userDetailsRepository.findByEmailWithRole(email);

        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "No user found with email: " + email);
            return ResponseEntity.status(404).body(error);
        }

        // BCrypt-encode and save the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userDetailsRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("email", email);
        response.put("newPassword", newPassword);
        response.put("name", user.getFirstname() + " " + user.getLastname());
        response.put("role", user.getUserRole() != null ? user.getUserRole().getRole() : "unknown");
        response.put("warning", "DELETE /api/test/resetPassword before deploying to production");

        return ResponseEntity.ok(response);
    }
}
