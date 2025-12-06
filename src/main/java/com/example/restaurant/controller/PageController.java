package com.example.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController extends BaseController {
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    // Menu and Reviews endpoints are handled by their respective controllers
    // MenuController handles /menu
    // ReviewController handles /reviews
}
