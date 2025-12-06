package com.example.restaurant.service;

import com.example.restaurant.model.MenuItem;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class MenuServiceImpl implements MenuService {
    
    // In-memory storage (replace with database in production)
    private final Map<Long, MenuItem> menuItems = new HashMap<>();
    private Long nextId = 1L;
    
    @PostConstruct
    public void init() {
        // Initialize with some sample menu items
        saveMenuItem(new MenuItem(null, "Caesar Salad", "Fresh romaine lettuce with Caesar dressing, croutons, and parmesan", 8.99, "Appetizers", "/images/salad.jpg"));
        saveMenuItem(new MenuItem(null, "Margherita Pizza", "Classic pizza with tomato sauce, mozzarella, and basil", 12.99, "Main Course", "/images/pizza.jpg"));
        saveMenuItem(new MenuItem(null, "Grilled Salmon", "Fresh salmon fillet with lemon butter sauce and seasonal vegetables", 22.99, "Main Course", "/images/salmon.jpg"));
        saveMenuItem(new MenuItem(null, "Tiramisu", "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone", 7.99, "Desserts", "/images/tiramisu.jpg"));
        saveMenuItem(new MenuItem(null, "Chocolate Lava Cake", "Warm chocolate cake with a molten center, served with vanilla ice cream", 8.99, "Desserts", "/images/lava-cake.jpg"));
        
        System.out.println("Initialized " + menuItems.size() + " menu items");
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return new ArrayList<>(menuItems.values());
    }

    @Override
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItems.values().stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MenuItem> getMenuItemById(Long id) {
        return Optional.ofNullable(menuItems.get(id));
    }

    @Override
    public MenuItem saveMenuItem(MenuItem menuItem) {
        if (menuItem.getId() == null) {
            menuItem.setId(nextId++);
        }
        menuItems.put(menuItem.getId(), menuItem);
        return menuItem;
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItems.remove(id);
    }

    @Override
    public List<String> getAllCategories() {
        return menuItems.values().stream()
                .map(MenuItem::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MenuItem> getMenuItemsByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return menuItems.values().stream()
                .filter(item -> ids.contains(item.getId()))
                .collect(Collectors.toList());
    }
}
