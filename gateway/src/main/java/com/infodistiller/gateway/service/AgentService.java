package com.infodistiller.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infodistiller.gateway.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AgentService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String agentServiceUrl = "http://agent-service:8000";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String generateQueryForTopic(String topic) {
        String url = agentServiceUrl + "/generate-interest-query";
        Map<String, String> requestBody = Map.of("topic", topic);

        Map<String, String> response = restTemplate.postForObject(url, requestBody, Map.class);

        return response != null ? response.get("query") : "";
    }

    public void requestBriefingGeneration(Integer userId) {
        try {
            Map<String, Object> jobPayload = Map.of("userId", userId);
            String message = objectMapper.writeValueAsString(jobPayload);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
            System.out.println(" [x] Sent job to queue for user_id: " + userId);
        } catch (Exception e) {
            System.err.println("Error sending message to RabbitMQ: " + e.getMessage());
        }
    }
}
