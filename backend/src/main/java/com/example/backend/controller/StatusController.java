package com.example.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatusController {

    // Endpoint to render the status page
    @GetMapping("/status")
    public String getStatusPage() {
        return "Backend is running"; // Returns the status.html page
    }
}
