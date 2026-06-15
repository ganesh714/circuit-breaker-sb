package com.example.circuit_breaker.service;

import com.example.circuit_breaker.dto.UserDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserDataService {

    private static final Logger logger = LoggerFactory.getLogger(UserDataService.class);
    private final RestTemplate restTemplate;

    public UserDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getFallbackUserData")
    public UserDTO fetchUser(String id) {
        logger.info("Attempting to fetch user data for id: {}", id);
        // Call the local mock service
        String url = "http://localhost:8080/mock-api/users/" + id;
        return restTemplate.getForObject(url, UserDTO.class);
    }

    public UserDTO getFallbackUserData(String id, Throwable throwable) {
        logger.error("Fallback activated for id: {} due to: {}", id, throwable.getMessage());
        return new UserDTO("default-id", "Default User", "default@example.com");
    }
}
