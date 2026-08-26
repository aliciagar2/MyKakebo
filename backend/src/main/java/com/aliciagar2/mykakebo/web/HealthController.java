package com.aliciagar2.mykakebo.web;


import com.aliciagar2.mykakebo.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok(healthService.getHealth());
    }

    private final HealthService healthService;

}
