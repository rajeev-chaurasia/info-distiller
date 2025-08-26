package com.infodistiller.gateway.controller;

import com.infodistiller.gateway.dtos.URLRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    private final RestTemplate restTemplate;

    @Autowired
    public AgentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/agent-health")
    public String getAgentHealth() {
        String agentServiceUrl = "http://agent-service:8000/health";
        return restTemplate.getForObject(agentServiceUrl, String.class);
    }

    @PostMapping("/summarize-url")
    public ResponseEntity<String> summarizeUrl(@RequestBody URLRequest request) {
        logger.info("Gateway received request to summarize URL: {}", request.getUrl());
        String agentServiceUrl = "http://agent-service:8000/api/v1/summarize-url";

        try {
            String response = restTemplate.postForObject(agentServiceUrl, request, String.class);
            logger.info("Successfully received summary from agent-service.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error calling agent-service for URL: {}", request.getUrl(), e);
            return ResponseEntity.status(500).body("{\"error\": \"Failed to communicate with the AI service.\"}");
        }
    }
}