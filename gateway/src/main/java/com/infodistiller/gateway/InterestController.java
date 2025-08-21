package com.infodistiller.gateway;

import com.infodistiller.gateway.dtos.URLRequest;
import com.infodistiller.gateway.entity.Interest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InterestController {

    private final InterestRepository interestRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(InterestController.class);

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
        String agentServiceUrl = "http://agent-service:8000/health";
        return restTemplate.getForObject(agentServiceUrl, String.class);
    }

    @PostMapping("/summarize-url")
    public String summarizeUrl(@RequestBody URLRequest request) {
        try {
            String agentServiceUrl = "http://agent-service:8000/api/v1/summarize-url";
            return restTemplate.postForObject(agentServiceUrl, request, String.class);
        } catch (Exception e) {
            logger.error("Error summarizing URL: {}", request.getUrl(), e);
            throw e;
        }
    }
}