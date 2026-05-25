package com.eforsch.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eforsch.ApiResponse;
import com.eforsch.Data;
import com.eforsch.Errors;
import com.eforsch.RoleResponse;
import com.eforsch.RolesApiResponse;
import com.eforsch.UserDetailsResponse;
import com.eforsch.UserResponse;
import com.eforsch.dto.EforschUser;
import com.eforsch.dto.User;
import com.eforsch.entity.UserDetails;
import com.eforsch.entity.UserRole;
import com.eforsch.entity.UserTokenEntity;
import com.eforsch.entity.EmailOtpEntity;
import com.eforsch.repository.UserDetailsRepository;
import com.eforsch.repository.UserRoleRepository;
import com.eforsch.repository.UserTokenRepository;
import com.eforsch.repository.EmailOtpRepository;
import com.eforsch.util.EmailUtil;
import com.eforsch.util.TokenUtils;
import com.eforsch.util.OtpUtil;
import com.eforsch.util.EmailValidator;
import com.eforsch.util.UserDetailsVO;



@Service
public class LoginService {

	@Autowired
	private UserDetailsRepository userDetailsRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
    private PasswordEncoder passwordEncoder; // ✅ Inject here

	@Autowired
	private EmailUtil emailUtil;
	
	/*
	 * @Autowired private JwtUtil jwtUtil;
	 */

	@Autowired
	private UserTokenRepository userTokenRepository;
	
	@Autowired
	private EmailOtpRepository emailOtpRepository;

	// Method to save the token into the user_tokens table
	public void storeToken(String userId, String token) {
		// Create a new UserToken entity
		UserTokenEntity userToken = new UserTokenEntity();
		userToken.setUserId(userId);
		userToken.setToken(token);
		userToken.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 3600000);
		userToken.setExpiresAt(expiresAt);
		userToken.setIsActive(true); // Set token as active by default
		// Save the token in the database
		userTokenRepository.save(userToken); // save() returns the saved entity
	}

	// Method to validate the token
	public boolean validateToken(String token) {
		Optional<UserTokenEntity> userTokenOpt = Optional.of(userTokenRepository.findByToken(token));

		// If token is not found, return false
		if (!userTokenOpt.isPresent()) {
			return false;
		}

		UserTokenEntity userToken = userTokenOpt.get();

		// Check if the token is active
		if (!userToken.getIsActive()) {
			return false;
		}

		// Check if the token has expired
		if (userToken.getExpiresAt() != null
				&& userToken.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
			return false; // Token has expired
		}

		// If all checks pass, the token is valid
		return true;
	}

	// Create or update a user
	public ApiResponse saveUser(UserDetailsVO userDetailsVO) {
		ApiResponse apiResponse = null;
		UserDetails userDetails2 = (UserDetails) userDetailsRepository.findByEmailWithRole(userDetailsVO.getEmail());

		if (userDetails2 != null && userDetails2.getEmail() != null) {
			apiResponse = new ApiResponse();
			apiResponse.setCode(409);
			apiResponse.setMessage("Email already in use");
			apiResponse.setStatus("error");
			return apiResponse;
		}

		apiResponse = validateUserDetails(userDetailsVO);
		if (apiResponse != null) {
			return apiResponse;
		}

		apiResponse = new ApiResponse();
		UserDetails userDetails = toEntity(userDetailsVO);
		userDetails = userDetailsRepository.save(userDetails);
		apiResponse.setStatus("success");
		apiResponse.setMessage("Registration successful");

		User user = new User();
		user.setId(userDetails.getUserId());
		user.setName(userDetails.getFirstname() + " " + userDetails.getLastname());
		user.setEmail(userDetails.getEmail());
		user.setRole(userDetailsVO.getRole());
		user.setGroupName(userDetails.getGroupName());
		user.setStatus(userDetails.getStatus());

		Data data = new Data();
		data.setExpiresIn(3600);
		data.setToken(TokenUtils.generateToken(userDetails.getEmail()));
		data.setUser(user);
		apiResponse.setData(data);
		apiResponse.setCode(0);

		try {
			emailUtil.sendRegistrationConfirmationEmail(userDetails.getEmail(), userDetails.getFirstname());
		} catch (Exception ex) {
			apiResponse.setMessage("Registration successful, but confirmation email could not be sent.");
		}

		return apiResponse;
	}

	
	public List<UserResponse> getUsersByRole(String roleName) {
        UserRole userRole = userRoleRepository.findByRole(roleName);
        if (userRole != null) {
            List<UserDetails> userDetailsList = userDetailsRepository.findByUserRole(userRole);
            
            
            List<UserResponse.UserInfo> users = userDetailsList.stream().map(user -> {
                return new UserResponse.UserInfo(
                    user.getUserId(),
                    user.getFirstname() + " " + user.getLastname(),
                    user.getEmail(),
                    user.getStatus()
                );
            }).collect(Collectors.toList());
            
            return Arrays.asList(new UserResponse("success", users));
            
        } else {
            return null; // Return an empty list if the role is not found
        }
    }
	
	
	public UserDetailsVO toVO(UserDetails userDetails) {
		UserDetailsVO userDetailsVO = new UserDetailsVO();
		userDetailsVO.setEmail(userDetails.getEmail());
		userDetailsVO.setPassword(userDetails.getPassword());
		userDetailsVO.setFname(userDetails.getFirstname());
		userDetailsVO.setLname(userDetails.getLastname());
		userDetailsVO.setRole(userDetails.getUserRole().getRolename());
		userDetailsVO.setGroupName(userDetails.getGroupName());
		return userDetailsVO;
	}

	public UserDetails toEntity(UserDetailsVO userDetailsVO) {
		UserDetails userDetails = new UserDetails();
		userDetails.setEmail(userDetailsVO.getEmail());
		userDetails.setPassword(passwordEncoder.encode(userDetailsVO.getPassword()));
		userDetails.setFirstname(userDetailsVO.getFname());
		userDetails.setLastname(userDetailsVO.getLname());

		// Fetch role from DB
		UserRole userRole = userRoleRepository.findByRole(userDetailsVO.getRole());
		userDetails.setUserRole(userRole);
		userDetails.setStatus("pending");
		userDetails.setGroupName(userDetailsVO.getGroupName());
		return userDetails;

	}

	// Regular expression for validating email
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

	// Method to validate email
	public static boolean isValidEmail(String email) {
		if (email == null || email.isEmpty()) {
			return false; // Null or empty strings are invalid
		}
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isValidPassword(String password) {
		if (password == null) {
			return false; // Null passwords are invalid
		}
		int n = password.length();
		if (n < 8) {
			return false;
		}
		return true;
	}

	private ApiResponse validateUserDetails(UserDetailsVO userDetailsVO) {

		ApiResponse apiResponse = null;
		Errors errors = null;
		if (!isValidEmail(userDetailsVO.getEmail())) {
			apiResponse = new ApiResponse();
			errors = new Errors();
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			errors.setEmail("A valid email address is required");
			apiResponse.setErrors(errors);

			return apiResponse;
		}
		if (!isValidPassword(userDetailsVO.getPassword())) {
			apiResponse = new ApiResponse();
			errors = new Errors();
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			errors.setEmail("Password must be at least 8 characters");
			apiResponse.setErrors(errors);

			return apiResponse;
		}

		if (userRoleRepository.findByRole(userDetailsVO.getRole()) == null) {
			apiResponse = new ApiResponse();
			errors = new Errors();
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			errors.setEmail("role name is wrong");
			apiResponse.setErrors(errors);

			return apiResponse;
		}

		if (!userDetailsVO.getPassword().equals(userDetailsVO.getRetypePassword())) {
			apiResponse = new ApiResponse();
			errors = new Errors();
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			errors.setEmail("Passwords should match");
			apiResponse.setErrors(errors);

			return apiResponse;
		}

		return null;
	}

	@Transactional(readOnly = true)
	public List<UserDetailsResponse> getAllUsers(User user1) {

	    List<UserDetails> userDetailsList = new ArrayList<>();

	    if (user1.getRole() != null && user1.getRole().equalsIgnoreCase("groupleader")) {
	        userDetailsList = userDetailsRepository.findByGroupNameWithRole(user1.getGroupName());
	    } else if (user1.getRole() != null && 
	              (user1.getRole().equalsIgnoreCase("admin") || user1.getRole().equalsIgnoreCase("labMgmt"))) {
	        userDetailsList = userDetailsRepository.findAllWithRole();
	    }

	    List<UserDetailsResponse> responseList = userDetailsList.stream()
	            .map(user -> new UserDetailsResponse(
	                    user.getUserId(),
	                    user.getFirstname(),
	                    user.getLastname(),
	                    user.getEmail(),
	                    user.getUserRole() != null ? user.getUserRole().getRole() : null,
	                    user.getGroupName(),
	                    user.getStatus(),
	                    true
	            ))
	            .collect(Collectors.toList());

	    for (UserDetailsResponse userDetailsResponse : responseList) {

	        if ("admin".equalsIgnoreCase(userDetailsResponse.getRole())) {
	            userDetailsResponse.setAction(false);
	        }

	        if ("Scientist".equalsIgnoreCase(userDetailsResponse.getRole())
	                && userDetailsResponse.getGroupName() != null) {
	            userDetailsResponse.setAction(
	                    user1.getGroupName() != null
	                            && userDetailsResponse.getGroupName().equalsIgnoreCase(user1.getGroupName()));
	        }

	        if ("groupleader".equalsIgnoreCase(userDetailsResponse.getRole())
	                && userDetailsResponse.getGroupName() != null
	                && user1.getGroupName() != null
	                && userDetailsResponse.getGroupName().equalsIgnoreCase(user1.getGroupName())) {
	            userDetailsResponse.setAction(false);
	        }
	    }

	    return responseList;
	}

	/**
	 * @param loginUser
	 * @return
	 */
	@Transactional
	public ApiResponse validateUser(EforschUser loginUser) {

		UserDetails userDetails2 = (UserDetails) userDetailsRepository.findByEmailWithRole(loginUser.getEmail());

		ApiResponse apiResponse = new ApiResponse();

		if (userDetails2 != null && userDetails2.getEmail() != null  && loginUser.getPassword() != null && 
				passwordEncoder.matches(loginUser.getPassword(), userDetails2.getPassword())) {
			apiResponse.setStatus("success");
			apiResponse.setMessage("Login successful");

			User user = new User();
			user.setName(userDetails2.getFirstname() + " " + userDetails2.getLastname());
			user.setId(userDetails2.getUserId());
			user.setEmail(userDetails2.getEmail());
			user.setRole(userDetails2.getUserRole().getRolename());
			user.setGroupName(userDetails2.getGroupName());
			user.setStatus(userDetails2.getStatus());

			Data data = new Data();
			data.setExpiresIn(3600);
			data.setToken(TokenUtils.generateToken(userDetails2.getEmail()));
			System.out.println("Token: " + data.getToken());
			data.setUser(user);
			apiResponse.setData(data);

			// Store the token in the database
			storeToken(userDetails2.getEmail(), data.getToken());

		} else {
			apiResponse.setStatus("error");
			apiResponse.setMessage("Invalid username or password");
			apiResponse.setCode(401);
		}

		return apiResponse;

	}

	// Get a user by ID
	public Optional<UserDetails> getUserById(Long userId) {
		return userDetailsRepository.findById(userId);
	}

	// Update user details by ID
	public UserDetails updateUser(Long userId, UserDetails userDetails) {
		if (userDetailsRepository.existsById(userId)) {
			userDetails.setUserId(userId);
			return userDetailsRepository.save(userDetails);
		}
		return null;
	}

	// Delete user by ID
	public void deleteUser(Long userId) {
		userDetailsRepository.deleteById(userId);
	}

	public RolesApiResponse getAllRoles() {

		List<UserRole> userRoles = userRoleRepository.findAll();

		List<RoleResponse> roles = userRoles.stream()
				.map(role -> new RoleResponse(role.getRole(), role.getRolename(), role.getDescription()))
				.collect(Collectors.toList());

		return new RolesApiResponse("success", roles);
	}

	/**
	 * Validates email format and sends OTP to the provided email address.
	 * OTP is valid for 3 minutes.
	 * 
	 * @param email the email address to validate and send OTP to
	 * @return ApiResponse with status and message
	 */
	public ApiResponse validateEmail(String email) {
		ApiResponse apiResponse = new ApiResponse();
		
		// Validate email format
		if (!EmailValidator.isValidEmail(email)) {
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			apiResponse.setMessage("Invalid email format");
			return apiResponse;
		}
		
		// Generate OTP
		String otp = OtpUtil.generateOtp();
		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime expiresAt = createdAt.plusMinutes(3); // OTP valid for 3 minutes
		
		// Delete existing OTP for this email if it exists
		emailOtpRepository.deleteByEmail(email);
		
		// Save new OTP to database
		EmailOtpEntity emailOtp = new EmailOtpEntity(email, otp, createdAt, expiresAt);
		emailOtpRepository.save(emailOtp);
		
		// Send OTP via email
		try {
			emailUtil.sendOtpEmail(email, otp);
			apiResponse.setCode(0);
			apiResponse.setStatus("success");
			apiResponse.setMessage("OTP sent successfully to " + email);
		} catch (Exception ex) {
			apiResponse.setCode(500);
			apiResponse.setStatus("error");
			apiResponse.setMessage("Failed to send OTP email. Please try again.");
		}
		
		return apiResponse;
	}

	/**
	 * Validates OTP for a given email address.
	 * OTP must be valid and not expired (within 3 minutes).
	 * 
	 * @param email the email address
	 * @param otp the OTP to validate
	 * @return ApiResponse with validation result
	 */
	public ApiResponse validateOTPforEmail(String email, String otp) {
		ApiResponse apiResponse = new ApiResponse();
		
		// Find OTP record for the email
		Optional<EmailOtpEntity> emailOtpOpt = emailOtpRepository.findByEmail(email);
		
		if (!emailOtpOpt.isPresent()) {
			apiResponse.setCode(404);
			apiResponse.setStatus("error");
			apiResponse.setMessage("No OTP found for this email. Please request a new OTP.");
			return apiResponse;
		}
		
		EmailOtpEntity emailOtpEntity = emailOtpOpt.get();
		
		// Check if OTP has expired
		if (LocalDateTime.now().isAfter(emailOtpEntity.getExpiresAt())) {
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			apiResponse.setMessage("OTP has expired. Please request a new OTP.");
			return apiResponse;
		}
		
		// Validate OTP
		if (!otp.equals(emailOtpEntity.getOtp())) {
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			apiResponse.setMessage("Invalid OTP. Please try again.");
			return apiResponse;
		}
		
		// Mark OTP as verified
		emailOtpEntity.setIsVerified(true);
		emailOtpRepository.save(emailOtpEntity);
		
		apiResponse.setCode(0);
		apiResponse.setStatus("success");
		apiResponse.setMessage("OTP validated successfully");
		return apiResponse;
	}

	/**
	 * Resends OTP to the provided email address.
	 * 
	 * @param email the email address to send OTP to
	 * @return ApiResponse with status and message
	 */
	public ApiResponse resendOTPForEmail(String email) {
		ApiResponse apiResponse = new ApiResponse();
		
		// Validate email format
		if (!EmailValidator.isValidEmail(email)) {
			apiResponse.setCode(400);
			apiResponse.setStatus("error");
			apiResponse.setMessage("Invalid email format");
			return apiResponse;
		}
		
		// Generate new OTP
		String otp = OtpUtil.generateOtp();
		LocalDateTime createdAt = LocalDateTime.now();
		LocalDateTime expiresAt = createdAt.plusMinutes(3); // OTP valid for 3 minutes
		
		// Delete existing OTP for this email
		emailOtpRepository.deleteByEmail(email);
		
		// Save new OTP to database
		EmailOtpEntity emailOtp = new EmailOtpEntity(email, otp, createdAt, expiresAt);
		emailOtpRepository.save(emailOtp);
		
		// Send OTP via email
		try {
			emailUtil.sendOtpEmail(email, otp);
			apiResponse.setCode(0);
			apiResponse.setStatus("success");
			apiResponse.setMessage("OTP resent successfully to " + email);
		} catch (Exception ex) {
			apiResponse.setCode(500);
			apiResponse.setStatus("error");
			apiResponse.setMessage("Failed to resend OTP email. Please try again.");
		}
		
		return apiResponse;
	}

}
