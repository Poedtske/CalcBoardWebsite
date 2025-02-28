package com.example.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    // Home page - accessible to everyone
    @GetMapping("/")
    public String homePage() {
        return "HomePage"; // Refers to homePage.html in src/main/resources/templates
    }

    // Users map page - requires authentication
    @GetMapping("/maps")
    public String usersMap() {
        return "UsersMap"; // Refers to UsersMap.html in src/main/resources/templates
    }

}
