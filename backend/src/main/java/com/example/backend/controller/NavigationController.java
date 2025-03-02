package com.example.backend.controller;

import com.example.backend.model.CalcBoardMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.backend.service.MapService;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NavigationController {

    private final MapService mapService;

    public NavigationController(MapService mapService) {
        this.mapService = mapService;
    }
    // Home page - accessible to everyone
    @GetMapping("/")
    public String homePage(Model model) {
        List<CalcBoardMap> maps = mapService.getAllMaps();
        model.addAttribute("maps", maps);
        return "HomePage";
    }

    // Users map page - requires authentication
    @GetMapping("/maps")
    public String usersMap() {
        return "UsersMap"; // Refers to UsersMap.html in src/main/resources/templates
    }

}
