package com.infodistiller.gateway.controller;

import com.infodistiller.gateway.entity.Interest;
import com.infodistiller.gateway.entity.User;
import com.infodistiller.gateway.repository.InterestRepository;
import com.infodistiller.gateway.service.AuthService;
import com.infodistiller.gateway.service.AgentService;
import com.infodistiller.gateway.dtos.InterestTopicRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interests")
public class InterestController {

    private final InterestRepository interestRepository;
    private final AuthService authService;
    private final AgentService agentService;

    @Autowired
    public InterestController(InterestRepository interestRepository, AuthService authService, AgentService agentService) {
        this.interestRepository = interestRepository;
        this.authService = authService;
        this.agentService = agentService;
    }

    @GetMapping
    public ResponseEntity<List<Interest>> getAllInterests(Principal principal) {
        User user = authService.findUserByEmail(principal.getName());
        return ResponseEntity.ok(interestRepository.findByUserId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Interest> createInterest(@RequestBody Interest interest, Principal principal) {
        User user = authService.findUserByEmail(principal.getName());
        interest.setUserId(user.getId());
        return ResponseEntity.ok(interestRepository.save(interest));
    }

    @PostMapping("/generate-query")
    public ResponseEntity<?> generateInterestQuery(@RequestBody InterestTopicRequest topicRequest) {
        try {
            String generatedQuery = agentService.generateQueryForTopic(topicRequest.getTopic());
            return ResponseEntity.ok(Map.of("query", generatedQuery));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate query from AI agent."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInterest(@PathVariable Integer id, Principal principal) {
        User user = authService.findUserByEmail(principal.getName());
        return interestRepository.findById(id)
                .map(interest -> {
                    if (!interest.getUserId().equals(user.getId())) {
                        return ResponseEntity.status(403).build();
                    }
                    interestRepository.delete(interest);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}