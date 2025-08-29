package com.infodistiller.gateway.controller;

import com.infodistiller.gateway.dtos.OtpRequest;
import com.infodistiller.gateway.dtos.OtpValidationRequest;
import com.infodistiller.gateway.entity.User;
import com.infodistiller.gateway.service.AuthService;
import com.infodistiller.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpRequest otpRequest) {
        try {
            authService.generateOtp(otpRequest.getEmail());
            return ResponseEntity.ok(Map.of("message", "OTP has been sent to your email address."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error sending email: " + e.getMessage()));
        }
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody OtpValidationRequest validationRequest) {
        try {
            User user = authService.validateOtpAndLogin(validationRequest);
            final String jwt = jwtUtil.generateToken(user);
            return ResponseEntity.ok(Map.of("token", jwt));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}