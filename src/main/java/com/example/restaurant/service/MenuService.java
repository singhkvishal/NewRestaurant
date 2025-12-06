package com.example.restaurant.service;

import com.example.restaurant.model.MenuItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MenuService {
    List<MenuItem> getAllMenuItems();
    List<MenuItem> getMenuItemsByCategory(String category);
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItem saveMenuItem(MenuItem menuItem);
    void deleteMenuItem(Long id);
    List<String> getAllCategories();
    
    /**
     * Get multiple menu items by their IDs
     * @param ids Set of menu item IDs to retrieve
     * @return List of menu items matching the provided IDs
     */
    List<MenuItem> getMenuItemsByIds(Set<Long> ids);
}
