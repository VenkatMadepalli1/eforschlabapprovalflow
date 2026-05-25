package com.eforsch.service;

import com.eforsch.dto.ProfileRequest;
import com.eforsch.dto.ProfileResponse;
import com.eforsch.entity.Profile;
import com.eforsch.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        // Validate required fields
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        // Check if profile already exists
        if (profileRepository.existsById(request.getUserId())) {
            throw new RuntimeException("Profile already exists for userId: " + request.getUserId());
        }

        // Create new profile
        Profile profile = new Profile();
        profile.setUserId(request.getUserId());
        profile.setTitle(request.getTitle());
        profile.setFirstName(request.getFirstName());
        profile.setSecondName(request.getSecondName());
        profile.setEmail(request.getEmail());
        profile.setLabName(request.getLabName());
        profile.setGroupLeader(request.getGroupLeader());
        profile.setRoomNumber(request.getRoomNumber());
        profile.setAddressLine1(request.getAddressLine1());
        profile.setAddressLine2(request.getAddressLine2());
        profile.setBuildingNumber(request.getBuildingNumber());
        profile.setStreetName(request.getStreetName());
        profile.setCity(request.getCity());
        profile.setRole(request.getRole());
        profile.setGroupName(request.getGroupName());

        Profile saved = profileRepository.save(profile);
        return toProfileResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        return toProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(String userId, ProfileRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        // Update fields
        if (request.getTitle() != null) {
            profile.setTitle(request.getTitle());
        }
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getSecondName() != null) {
            profile.setSecondName(request.getSecondName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            profile.setEmail(request.getEmail());
        }
        if (request.getLabName() != null) {
            profile.setLabName(request.getLabName());
        }
        if (request.getGroupLeader() != null) {
            profile.setGroupLeader(request.getGroupLeader());
        }
        if (request.getRoomNumber() != null) {
            profile.setRoomNumber(request.getRoomNumber());
        }
        if (request.getAddressLine1() != null) {
            profile.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            profile.setAddressLine2(request.getAddressLine2());
        }
        if (request.getBuildingNumber() != null) {
            profile.setBuildingNumber(request.getBuildingNumber());
        }
        if (request.getStreetName() != null) {
            profile.setStreetName(request.getStreetName());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getRole() != null) {
            profile.setRole(request.getRole());
        }
        if (request.getGroupName() != null) {
            profile.setGroupName(request.getGroupName());
        }

        Profile updated = profileRepository.save(profile);
        return toProfileResponse(updated);
    }

    @Transactional
    public void deleteProfile(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        profileRepository.delete(profile);
    }

    private ProfileResponse toProfileResponse(Profile profile) {
        return new ProfileResponse(
                profile.getUserId(),
                profile.getTitle(),
                profile.getFirstName(),
                profile.getSecondName(),
                profile.getEmail(),
                profile.getLabName(),
                profile.getGroupLeader(),
                profile.getRoomNumber(),
                profile.getAddressLine1(),
                profile.getAddressLine2(),
                profile.getBuildingNumber(),
                profile.getStreetName(),
                profile.getCity(),
                profile.getRole(),
                profile.getGroupName(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
