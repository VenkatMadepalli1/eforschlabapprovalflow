package com.eforsch.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.ApiResponse;
import com.eforsch.RolesApiResponse;
import com.eforsch.UserDetailsResponse;
import com.eforsch.UserResponse;
import com.eforsch.dto.EforschUser;
import com.eforsch.dto.User;
import com.eforsch.dto.EmailValidationRequest;
import com.eforsch.dto.OtpValidationRequest;
import com.eforsch.entity.UserDetails;
import com.eforsch.repository.UserTokenRepository;
import com.eforsch.service.ForgotPasswordService;
import com.eforsch.service.LoginService;
import com.eforsch.util.LogoutRequest;
import com.eforsch.util.UserDetailsVO;
import com.eforsch.dto.ForgotPasswordRequest;
import com.eforsch.dto.OtpRequest;
import com.eforsch.dto.ResetPasswordRequest;

@RestController
@RequestMapping("/auth")

public class LoginController {
	
	@Autowired
    private LoginService userDetailsService;
	
	@Autowired
	private UserTokenRepository userTokenRepository;
	
	 
    @Autowired
    private ForgotPasswordService forgotPasswordService;
	
	@PostMapping("/login")
	public ApiResponse authenticateUser(@RequestBody EforschUser loginUser) {

		return userDetailsService.validateUser(loginUser);

	}
	
	// Create or update user
    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserDetailsVO userDetailsVO) {
    	ApiResponse aApiResponse = userDetailsService.saveUser(userDetailsVO);
        return new ResponseEntity<>(aApiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<RolesApiResponse> getRoles() {
        return ResponseEntity.ok(userDetailsService.getAllRoles());
    }
    
    // Get all users
    @PostMapping("/getAllUsers")
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers(@RequestBody User user) {
        List<UserDetailsResponse> users = userDetailsService.getAllUsers(user);
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

 
    @GetMapping("/getUsersByRole/{roleName}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String roleName) {
        List<UserResponse> users = userDetailsService.getUsersByRole(roleName);
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }
    
    // Get user by ID
    @GetMapping("/getUserById")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long userId) {
        Optional<UserDetails> user = userDetailsService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update user by ID
    @PutMapping("/updateUser")
    public ResponseEntity<UserDetails> updateUser(@PathVariable Long userId, @RequestBody UserDetails userDetails) {
        UserDetails updatedUser = userDetailsService.updateUser(userId, userDetails);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    // Delete user by ID
    @DeleteMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userDetailsService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {
        String token = logoutRequest.getToken();
        
        // Logic to validate and process the token
       if (!userDetailsService.validateToken(token)) {
            // Perform logout logic (e.g., invalidate token in database or cache)
            invalidateToken(token);
            return ResponseEntity.ok(new ApiResponse("success", "User logged out successfully"));
        } else {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("error", "Invalid or expired token"));
        }
    }



    private void invalidateToken(String token) {
           // Logic to invalidate token
    		userTokenRepository.deleteByToken(token);
    	
    }
   
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();
        ApiResponse response = forgotPasswordService.handleForgotPassword(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();
        ApiResponse response = forgotPasswordService.resendOtp(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/otp")
    public ResponseEntity<ApiResponse> authenticateOtp(@RequestBody OtpRequest request) {
        String otp = request.getOtp();
        String email = request.getEmail();
        ApiResponse response = forgotPasswordService.validateOtp(email, otp);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();
        String email = request.getEmail();
        ApiResponse response = forgotPasswordService.resetPassword(email, newPassword, confirmPassword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API endpoint to validate email and send OTP.
     * This endpoint checks if the email format is valid and sends an OTP to the email.
     * OTP is valid for 3 minutes.
     * 
     * @param request EmailValidationRequest containing the email address
     * @return ApiResponse with status and message
     */
    @PostMapping("/validateEmail")
    public ResponseEntity<ApiResponse> validateEmail(@RequestBody EmailValidationRequest request) {
        ApiResponse response = userDetailsService.validateEmail(request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API endpoint to validate OTP for a given email.
     * This endpoint validates the OTP against the stored OTP for the email
     * and checks if the OTP is within the 3-minute validity window.
     * 
     * @param request OtpValidationRequest containing email and OTP
     * @return ApiResponse with validation result
     */
    @PostMapping("/validateOTP")
    public ResponseEntity<ApiResponse> validateOTP(@RequestBody OtpValidationRequest request) {
        ApiResponse response = userDetailsService.validateOTPforEmail(request.getEmail(), request.getOtp());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API endpoint to resend OTP to a given email.
     * This endpoint generates a new OTP and sends it to the email address.
     * The old OTP will be invalidated.
     * 
     * @param request EmailValidationRequest containing the email address
     * @return ApiResponse with status and message
     */
    @PostMapping("/resendOTPForEmail")
    public ResponseEntity<ApiResponse> resendOTPForEmail(@RequestBody EmailValidationRequest request) {
        ApiResponse response = userDetailsService.resendOTPForEmail(request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

}
