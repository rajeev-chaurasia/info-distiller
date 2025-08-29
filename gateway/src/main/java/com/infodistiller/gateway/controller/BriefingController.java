package com.infodistiller.gateway.controller;

import com.infodistiller.gateway.entity.Selection;
import com.infodistiller.gateway.entity.User;
import com.infodistiller.gateway.service.AuthService;
import com.infodistiller.gateway.service.BriefingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/briefing")
public class BriefingController {

    private final BriefingService briefingService;
    private final AuthService authService;

    @Autowired
    public BriefingController(BriefingService briefingService, AuthService authService) {
        this.briefingService = briefingService;
        this.authService = authService;
    }

    @GetMapping("/today")
    public ResponseEntity<List<Selection>> getTodaysBriefing(Principal principal) {
        String userEmail = principal.getName();
        User user = authService.findUserByEmail(userEmail);
        List<Selection> selections = briefingService.getTodaysBriefing(user.getId());
        return ResponseEntity.ok(selections);
    }
}