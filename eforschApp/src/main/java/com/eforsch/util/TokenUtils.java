package com.eforsch.util;

import java.security.SecureRandom;

public class TokenUtils {

    public static String generateToken(String userId) {
       
		/*
		 * SecureRandom random = new SecureRandom(); byte[] tokenBytes = new byte[128];
		 * // 128 bytes = 256 hex chars random.nextBytes(tokenBytes);
		 * 
		 * StringBuilder token = new StringBuilder(); for (byte b : tokenBytes) {
		 * token.append(String.format("%02x", b)); // Convert each byte to hex }
		 * 
		 * return token.toString();
		 */
    	
    	SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder(15);

        for (int i = 0; i < 15; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }

        return token.toString();
    }
	
    
    
	
}
