package com.example.backend.controller;

import com.example.backend.model.CalcBoardMap;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.backend.service.MapService;

import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class NavigationController {

    private final MapService mapService;
    private final UserService userService;

    public NavigationController(MapService mapService, UserService userService) {
        this.userService = userService;
        this.mapService = mapService;
    }

    // Home page - accessible to everyone
    @GetMapping("/")
    public String homePage(Model model) {
        List<CalcBoardMap> maps = mapService.getAllMaps();
        model.addAttribute("maps", maps);
        return "HomePage";
    }

    @GetMapping("/maps")
    public String usersMap(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/"; // Redirect unauthenticated
        }

        String userEmail = authentication.getName(); // Extract email (or username)
        System.out.println("Authenticated user: " + userEmail);

        User user = userService.findUserByEmail(userEmail);
        List<CalcBoardMap> userMaps = mapService.getUserMaps(user.getEmail());
        model.addAttribute("maps", userMaps);

        return "UsersMap";
    }

}
