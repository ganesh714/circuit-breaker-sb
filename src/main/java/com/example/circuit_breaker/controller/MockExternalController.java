package com.example.circuit_breaker.controller;

import com.example.circuit_breaker.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/mock-api")
public class MockExternalController {

    private final AtomicInteger requestCount = new AtomicInteger(0);

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        int count = requestCount.incrementAndGet();
        // Simulate failure: fail 70% of the time to easily trip the circuit breaker
        if (count % 10 < 7) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(new UserDTO(id, "Mock User " + id, "mock" + id + "@example.com"));
    }
}
