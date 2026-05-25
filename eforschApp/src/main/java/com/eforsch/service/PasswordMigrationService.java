package com.eforsch.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eforsch.ApiResponse;
import com.eforsch.entity.UserDetails;
import com.eforsch.repository.UserDetailsRepository;

@Service
public class PasswordMigrationService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiResponse migratePasswords() {
        List<UserDetails> users = userDetailsRepository.findAll();
        
        int migrated = 0;
        int skipped = 0;

        for (UserDetails user : users) {
            String currentPassword = user.getPassword();

            // Skip if already BCrypt encoded (starts with $2a$)
            if (currentPassword != null && currentPassword.startsWith("$2a$")) {
                skipped++;
                continue;
            }

            // Encode plain text password and save
            user.setPassword(passwordEncoder.encode(currentPassword));
            userDetailsRepository.save(user);
            migrated++;
        }

        String message = "Migration complete. Migrated: " + migrated + ", Skipped (already encoded): " + skipped;
        return new ApiResponse(200, "SUCCESS", message);
    }
}