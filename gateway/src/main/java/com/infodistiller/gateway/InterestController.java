package com.infodistiller.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InterestController {

    private final InterestRepository interestRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public InterestController(InterestRepository interestRepository, RestTemplate restTemplate) {
        this.interestRepository = interestRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/interests")
    public List<Interest> getAllInterests() {
        return interestRepository.findAll();
    }

    @GetMapping("/agent-health")
    public String getAgentHealth() {
        // The URL uses the service name 'agent-service' from docker-compose.yml
        String agentServiceUrl = "http://agent-service:8000/health";
        // Use the RestTemplate to make a GET request and return the response as a String
        return restTemplate.getForObject(agentServiceUrl, String.class);
    }
}