
package com.eforsch.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

	@Autowired
	private JavaMailSender mailSender;

	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Password Reset OTP");
		message.setText("Your OTP for password reset is: " + otp);
		mailSender.send(message);
	}

	public void sendRegistrationConfirmationEmail(String toEmail, String firstName) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Welcome to eForsch");
		message.setText("Dear " + firstName + ",\n\n"
				+ "Your registration with eForsch has been received successfully. "
				+ "Our team will review your account and notify you once it is approved.\n\n"
				+ "Regards,\n"
				+ "eForsch Team");
		mailSender.send(message);
	}
}
