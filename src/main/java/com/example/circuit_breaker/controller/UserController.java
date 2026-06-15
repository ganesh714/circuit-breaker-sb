package com.example.circuit_breaker.controller;

import com.example.circuit_breaker.dto.UserDTO;
import com.example.circuit_breaker.service.UserDataService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserDataService userDataService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public UserController(UserDataService userDataService, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.userDataService = userDataService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        UserDTO user = userDataService.fetchUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/circuit-breaker/state")
    public ResponseEntity<String> getCircuitBreakerState() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        return ResponseEntity.ok(circuitBreaker.getState().toString());
    }

    @GetMapping("/circuit-breaker/metrics")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerMetrics() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        
        Map<String, Object> metricsMap = new HashMap<>();
        metricsMap.put("failureRate", metrics.getFailureRate());
        metricsMap.put("failedCalls", metrics.getNumberOfFailedCalls());
        metricsMap.put("successfulCalls", metrics.getNumberOfSuccessfulCalls());
        metricsMap.put("notPermittedCalls", metrics.getNumberOfNotPermittedCalls());
        metricsMap.put("state", circuitBreaker.getState().toString());
        
        return ResponseEntity.ok(metricsMap);
    }
}
