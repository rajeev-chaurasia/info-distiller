package com.infodistiller.gateway.service;

import com.infodistiller.gateway.dtos.OtpValidationRequest;
import com.infodistiller.gateway.entity.Otp;
import com.infodistiller.gateway.entity.User;
import com.infodistiller.gateway.repository.OtpRepository;
import com.infodistiller.gateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final Random random = new SecureRandom();

    @Autowired
    public AuthService(UserRepository userRepository, OtpRepository otpRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public void generateOtp(String email) {
        String otpCode = String.format("%06d", random.nextInt(999999));

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(otpCode);
        otp.setExpiresAt(OffsetDateTime.now().plusMinutes(10)); // OTP is valid for 10 minutes

        otpRepository.save(otp);
        emailService.sendOtpEmail(email, otpCode);
    }

    public User validateOtpAndLogin(OtpValidationRequest validationRequest) {
        Otp otp = otpRepository.findFirstByEmailOrderByExpiresAtDesc(validationRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("OTP not found or invalid."));

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now()) || !otp.getCode().equals(validationRequest.getOtp())) {
            throw new IllegalArgumentException("OTP is invalid or has expired.");
        }

        // OTP is valid. Find or create the user.
        Optional<User> userOptional = userRepository.findByEmail(validationRequest.getEmail());

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            // If user does not exist, create a new one
            User newUser = new User();
            newUser.setEmail(validationRequest.getEmail());
            newUser.setRssPrivateToken(UUID.randomUUID().toString());
            return userRepository.save(newUser);
        }
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
}