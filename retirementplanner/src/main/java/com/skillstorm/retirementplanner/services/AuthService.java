package com.skillstorm.retirementplanner.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.LoginRequest;
import com.skillstorm.retirementplanner.dtos.RegisterRequest;
import com.skillstorm.retirementplanner.dtos.ResendRequest;
import com.skillstorm.retirementplanner.dtos.VerifyRequest;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class AuthService {

    /**
     * AuthService: 
     * All auth business logic is implemented here.
     * No email verification (as of now): accounts are usable right after signup. 
     * Throws RuntimeException on failures; the controller maps those to 400.
     *
     * Methods:
     * POST/Create:
     * - signup(RegisterRequest input)
     *
     * Auth:
     * - authenticate(LoginRequest input)
     */
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;


    // fields for email verification
    private final EmailService emailService;
    private static final int CODE_TTL_MINUTES = 15;
    private static final int MAX_VERIFICATION_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    // parameterized constructor
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.emailService = emailService;
    }

    /**
     * authenticate:
     * It verifies login credentials (email OR username + password).
     *
     * args:
     * - LoginRequest input: identifier (email or username) + password
     *
     * returns:
     * - User: the authenticated user
     * 
     * throws:
     * - RuntimeException: if the user is not found or the password is wrong
     */
    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmailOrUsername(request.identifier(), request.identifier())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
        // email verification
        if(!user.isEnabled()){
            throw new RuntimeException("Account not verified. Please check your email for the verification code");
        }
        authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
        return user;
    }

    /**
     * signup:
     * creates a new user with a BCrypt-hashed password.
     *
     * args:
     * - RegisterRequest input: username, email, password
     *
     * returns:
     * - User: the saved user
     * 
     * throws:
     * - RuntimeException: if email or username already exists
     */
    public User signup(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already in use");
        }
        if(userRepository.existsByUsername(request.username())){
            throw new RuntimeException("Username already in use");
        }
        User user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()));
        user.setEnabled(false);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES));
        user.setVerificationAttempts(0);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    /**
     * verifyUser:
     * Confirms the emailed 6 digit code and set the enable property
     *
     * args:
     * - VerifyRequest request: email + verificationCode
     *
     * throws:
     * - RuntimeException: user not found, already verified, code expired, or code mismatch
     */
    public void verifyUser(VerifyRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }
        if (user.getVerificationCodeExpiresAt() == null
                || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired. Please request a new one.");
        }
        if (user.getVerificationAttempts() >= MAX_VERIFICATION_ATTEMPTS) {
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            userRepository.save(user);
            throw new RuntimeException("Too many failed attempts. Please request a new verification code.");
        }
        if (!user.getVerificationCode().equals(request.verificationCode())) {
            user.setVerificationAttempts(user.getVerificationAttempts() + 1);
            userRepository.save(user);
            throw new RuntimeException("Invalid verification code");
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        user.setVerificationAttempts(0);
        userRepository.save(user);
    }

    /**
     * resendVerificationCode:
     * Issues a fresh code and emails it again 
     *
     * args:
     * - ResendRequest request: email
     *
     * throws:
     * - RuntimeException: user not found or already verified
     */
    public void resendVerificationCode(ResendRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES));
        user.setVerificationAttempts(0);
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    /**
     * sendVerificationEmail:
     * Builds the HTML email body and hands it to EmailService (private helper).
     * In case of failure -> RuntimeException.
     * Signup, resend reports a clear error msg instead of silently succeeding with no email sent
     *
     * args:
     * - User user: the recipient, whose current code is embedded in the email
     */
    private void sendVerificationEmail(User user) {
        String subject = "Verify your Retirement Planner account";
        String htmlBody = "<html><body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color:#f5f5f5; padding:20px;\">"
                + "<h2 style=\"color:#333;\">Welcome to Retirement Planner!</h2>"
                + "<p style=\"font-size:16px;\">Enter this code to verify your account "
                + "(valid for " + CODE_TTL_MINUTES + " minutes):</p>"
                + "<div style=\"background-color:#fff; padding:20px; border-radius:5px;\">"
                + "<p style=\"font-size:24px; font-weight:bold; color:#007bff; letter-spacing:3px;\">"
                + user.getVerificationCode() + "</p>"
                + "</div></div></body></html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email. Please try again later.");
        }
    }

    /**
     * generateVerificationCode:
     * Returns a random 6-digit code as a String (private helper).
     *
     * returns:
     * - String: a value in the range "100000".."999999"
     */
    private String generateVerificationCode() {
        int code = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
