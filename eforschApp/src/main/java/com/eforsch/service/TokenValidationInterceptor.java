package com.eforsch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService; // Service to validate the token

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    /*
    	// Skip the validation for /login and /uploadexcel endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login") || requestURI.contains("/createUser") || requestURI.contains("/upload")
        		|| requestURI.contains("swagger") || requestURI.contains("/v3")) {
            return true; // Allow the request to proceed
        }

        // Extract token from the Authorization header (assuming Bearer token format)
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is missing or invalid");
            return false; // Block request if token is missing or invalid
        }

        // Validate the token using the service
        if (!loginService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return false; // Block request if token is invalid or expired
        }
     */
        return true; // Allow the request to proceed if token is valid
    }
}
