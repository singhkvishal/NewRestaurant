package com.example.restaurant.controller;

import com.example.restaurant.model.MenuItem;
import com.example.restaurant.service.MenuService;
import com.example.restaurant.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController extends BaseController {

    private final MenuService menuService;
    private final ReviewService reviewService;

    public HomeController(MenuService menuService, ReviewService reviewService) {
        this.menuService = menuService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String home(Model model) {
        System.out.println("\n=== Home Controller Started ===");
        
        
        try {
            // 1. Get all menu items
            System.out.println("1. Getting all menu items...");
            List<MenuItem> allItems = new ArrayList<>();
            try {
                allItems = menuService.getAllMenuItems() != null ? menuService.getAllMenuItems() : new ArrayList<>();
                System.out.println("   - Success: Found " + allItems.size() + " menu items");
            } catch (Exception e) {
                System.err.println("   - Error getting menu items: " + e.getMessage());
                e.printStackTrace();
                allItems = new ArrayList<>();
            }
            
            // 2. Get main course items
            System.out.println("2. Getting main course items...");
            List<MenuItem> mainCourseItems = new ArrayList<>();
            try {
                mainCourseItems = menuService.getMenuItemsByCategory("Main Course") != null ? 
                    menuService.getMenuItemsByCategory("Main Course") : new ArrayList<>();
                System.out.println("   - Found " + mainCourseItems.size() + " main course items");
            } catch (Exception e) {
                System.err.println("   - Error getting main course items: " + e.getMessage());
                e.printStackTrace();
                mainCourseItems = new ArrayList<>();
            }
            
            // 3. Prepare featured items
            System.out.println("3. Preparing featured items...");
            List<MenuItem> featuredItems = new ArrayList<>();
            List<MenuItem> sourceList = (mainCourseItems != null && !mainCourseItems.isEmpty()) ? 
                mainCourseItems : (allItems != null ? allItems : new ArrayList<>());
                
            System.out.println("   - Using " + (sourceList == mainCourseItems ? "main course items" : "all items") + " as source");
            
            // 4. Add up to 3 items to featured items
            int count = 0;
            if (sourceList != null) {
                for (MenuItem item : sourceList) {
                    if (count >= 3) break;
                    if (item != null) {
                        featuredItems.add(item);
                        count++;
                    }
                }
            }
            System.out.println("   - Added " + featuredItems.size() + " items to featured list");
            
            // 5. Get reviews
            System.out.println("4. Getting reviews...");
            List<?> reviews = new ArrayList<>();
            Double averageRating = 0.0;
            try {
                reviews = reviewService.getApprovedReviews() != null ? 
                    reviewService.getApprovedReviews() : new ArrayList<>();
                // getAverageRating() returns a primitive double which can't be null
                averageRating = reviewService.getAverageRating();
                System.out.println("   - Found " + reviews.size() + " approved reviews");
                System.out.println("   - Average rating: " + averageRating);
            } catch (Exception e) {
                System.err.println("   - Error getting reviews: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 6. Add attributes to model - ensure none are null
            System.out.println("5. Adding attributes to model...");
            model.addAttribute("featuredItems", featuredItems != null ? featuredItems : new ArrayList<>());
            model.addAttribute("reviews", reviews != null ? reviews : new ArrayList<>());
            model.addAttribute("averageRating", averageRating != null ? averageRating : 0.0);
            
            System.out.println("6. Rendering home page template...");
            return "home";
        } catch (Exception e) {
            // Log the error and return a safe state
            System.err.println("Error in home controller: " + e.getMessage());
            e.printStackTrace();
            
            // Add empty lists to prevent template errors
            model.addAttribute("featuredItems", Collections.emptyList());
            model.addAttribute("reviews", Collections.emptyList());
            model.addAttribute("averageRating", 0.0);
            
            return "home";
        }
    }

    // Moved to PageController to avoid duplicate mappings
}
