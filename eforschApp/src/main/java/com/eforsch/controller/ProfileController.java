package com.eforsch.controller;

import com.eforsch.dto.ProfileRequest;
import com.eforsch.dto.ProfileResponse;
import com.eforsch.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * CREATE PROFILE API
     * POST /api/profile
     * Creates a new user profile with user information
     */
    @PostMapping(name = "createProfile")
    public ResponseEntity<?> createProfile(@Valid @RequestBody ProfileRequest request) {
        try {
            if (request.getUserId() == null || request.getUserId().isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "userId is required");
                error.put("code", 400);
                return ResponseEntity.badRequest().body(error);
            }
            if (request.getFirstName() == null || request.getFirstName().isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "firstName is required");
                error.put("code", 400);
                return ResponseEntity.badRequest().body(error);
            }
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "email is required");
                error.put("code", 400);
                return ResponseEntity.badRequest().body(error);
            }

            ProfileResponse profile = profileService.createProfile(request);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Profile created successfully");
            response.put("code", 201);

            Map<String, Object> data = new HashMap<>();
            data.put("userId", profile.getUserId());
            response.put("data", data);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", ex.getMessage());
            error.put("code", 400);
            return ResponseEntity.badRequest().body(error);

        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", ex.getMessage());
            error.put("code", 409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", "Internal server error");
            error.put("code", 500);
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET PROFILE API
     * GET /api/profile/{userId}
     * Retrieves a user profile by userId
     */
    @GetMapping(value = "/{userId}", name = "getProfile")
    public ResponseEntity<?> getProfile(@PathVariable String userId) {
        try {
            if (userId == null || userId.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "userId is required");
                error.put("code", 400);
                return ResponseEntity.badRequest().body(error);
            }

            ProfileResponse profile = profileService.getProfile(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("code", 200);
            response.put("data", profile);

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", ex.getMessage());
            error.put("code", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", "Internal server error");
            error.put("code", 500);
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * UPDATE PROFILE API
     * PUT /api/profile/{userId}
     * Updates an existing user profile
     */
    @PutMapping(value = "/{userId}", name = "updateProfile")
    public ResponseEntity<?> updateProfile(@PathVariable String userId, @Valid @RequestBody ProfileRequest request) {
        try {
            if (userId == null || userId.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "userId is required");
                error.put("code", 400);
                return ResponseEntity.badRequest().body(error);
            }

            ProfileResponse profile = profileService.updateProfile(userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Profile updated successfully");
            response.put("code", 200);
            response.put("data", profile);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", ex.getMessage());
            error.put("code", 400);
            return ResponseEntity.badRequest().body(error);

        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", ex.getMessage());
            error.put("code", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", "Internal server error");
            error.put("code", 500);
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE PROFILE API
     * DELETE /api/profile/{userId}
     * Deletes a user profile by userId
     */
    @DeleteMapping(value = "/{userId}", name = "deleteProfile")
    public ResponseEntity<?> deleteProfile(@PathVariable String userId) {
        try {
            if (userId == null || userId.isBlank()) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "userId is required");
                error.put("code", 400);
                return ResponseEntity.badRequest().body(error);
            }

            profileService.deleteProfile(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Profile deleted successfully");
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", ex.getMessage());
            error.put("code", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", "Internal server error");
            error.put("code", 500);
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
