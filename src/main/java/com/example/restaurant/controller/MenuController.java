package com.example.restaurant.controller;

import com.example.restaurant.service.MenuService;
import com.example.restaurant.model.MenuItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/menu")
public class MenuController extends BaseController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public String menu(Model model) {
        List<String> categories = menuService.getAllCategories();
        model.addAttribute("categories", categories);
        
        // Group menu items by category
        Map<String, List<MenuItem>> menuItemsByCategory = new LinkedHashMap<String, List<MenuItem>>();
        for (String category : categories) {
            menuItemsByCategory.put(category, menuService.getMenuItemsByCategory(category));
        }
        
        model.addAttribute("menuItemsByCategory", menuItemsByCategory);
        return "menu/menu";
    }

    @GetMapping("/category/{category}")
    public String menuByCategory(@PathVariable String category, Model model) {
        List<String> categories = menuService.getAllCategories();
        model.addAttribute("categories", categories);
        
        // For a specific category, we'll only show that category's items
        Map<String, List<MenuItem>> menuItemsByCategory = new LinkedHashMap<>();
        menuItemsByCategory.put(category, menuService.getMenuItemsByCategory(category));
        
        model.addAttribute("menuItemsByCategory", menuItemsByCategory);
        model.addAttribute("currentCategory", category);
        return "menu/menu";
    }

    @GetMapping("/item/{id}")
    public String menuItemDetails(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("menuItem", menuService.getMenuItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID: " + id)));
                
        // Add CSRF token to the model
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        
        return "menu/item-details";
    }

    @GetMapping("/search")
    public String searchMenu(@RequestParam String query, Model model) {
        // This is a simple search implementation
        // In a real application, you would implement a more sophisticated search
        model.addAttribute("searchQuery", query);
        model.addAttribute("categories", menuService.getAllCategories());
        return "menu/search-results";
    }
}
