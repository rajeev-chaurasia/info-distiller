package com.infodistiller.gateway.controller;

import com.infodistiller.gateway.dtos.NotificationRequest;
import com.infodistiller.gateway.entity.Selection;
import com.infodistiller.gateway.entity.User;
import com.infodistiller.gateway.service.AgentService;
import com.infodistiller.gateway.service.AuthService;
import com.infodistiller.gateway.service.BriefingService;
import com.infodistiller.gateway.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/briefing")
public class BriefingController {

    private final BriefingService briefingService;
    private final AuthService authService;
    private final AgentService agentService;
    private final EmailService emailService;

    @Autowired
    public BriefingController(BriefingService briefingService, AuthService authService, AgentService agentService, EmailService emailService) {
        this.briefingService = briefingService;
        this.authService = authService;
        this.agentService = agentService;
        this.emailService = emailService;
    }

    @GetMapping("/today")
    public ResponseEntity<List<Selection>> getTodaysBriefing(Principal principal) {
        String userEmail = principal.getName();
        User user = authService.findUserByEmail(userEmail);
        List<Selection> selections = briefingService.getTodaysBriefing(user.getId());
        return ResponseEntity.ok(selections);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateBriefing(Principal principal) {
        User user = authService.findUserByEmail(principal.getName());
        agentService.requestBriefingGeneration(user.getId());
        return ResponseEntity.accepted().body(Map.of("message", "Briefing generation has started. You will be notified when it's ready."));
    }

    @PostMapping("/notify")
    public ResponseEntity<?> notifyUserBriefingReady(@RequestBody NotificationRequest notificationRequest) {
        try {
            User user = authService.findUserById(notificationRequest.getUserId());
            String briefingUrl = "http://localhost:3000/briefing";
            emailService.sendBriefingReadyEmail(user.getEmail(), briefingUrl);
            return ResponseEntity.ok(Map.of("message", "Notification sent."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}