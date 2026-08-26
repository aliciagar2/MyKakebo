package com.aliciagar2.mykakebo.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public String getHealth() {
        return "OK";
    }
}