package com.example.restaurant.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {
    
    @ModelAttribute
    public void addCurrentPath(Model model, HttpServletRequest request) {
        String currentPath = request != null ? request.getRequestURI() : "";
        model.addAttribute("currentPath", currentPath);
    }
}
