package com.example.restaurant.service;

import com.example.restaurant.model.MenuItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.io.Serializable;

import java.math.BigDecimal;
import java.util.*;

@Service
@SessionScope
public class CartService {
    private final Map<Long, CartItem> items = new HashMap<>();

    public void addItem(MenuItem menuItem, int quantity) {
        if (menuItem == null) {
            System.out.println("Error: Cannot add null menu item to cart");
            return;
        }
        
        System.out.println("Adding item to cart - ID: " + menuItem.getId() + 
                         ", Name: " + menuItem.getName() + 
                         ", Price: " + menuItem.getPrice() + 
                         ", Quantity: " + quantity);
                         
        items.compute(menuItem.getId(), (id, existingItem) -> {
            if (existingItem != null) {
                existingItem.incrementQuantity(quantity);
                System.out.println("Updated existing item. New quantity: " + existingItem.getQuantity() + 
                                 ", New subtotal: " + existingItem.getSubtotal());
                return existingItem;
            }
            CartItem newItem = new CartItem(menuItem, quantity);
            System.out.println("Added new item to cart. Subtotal: " + newItem.getSubtotal());
            return newItem;
        });
        
        System.out.println("Current cart size: " + items.size() + ", Total items: " + getItemCount());
        System.out.println("Current cart total: " + getTotal());
    }

    public void removeItem(Long menuItemId) {
        items.remove(menuItemId);
    }

    public void updateQuantity(Long menuItemId, int quantity) {
        if (quantity <= 0) {
            removeItem(menuItemId);
            return;
        }
        items.computeIfPresent(menuItemId, (id, item) -> {
            item.setQuantity(quantity);
            return item;
        });
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public BigDecimal getTotal() {
        System.out.println("Calculating cart total...");
        System.out.println("Number of items in cart: " + items.size());
        
        if (items.isEmpty()) {
            System.out.println("Cart is empty, returning 0");
            return BigDecimal.ZERO.setScale(2);
        }
        
        BigDecimal total = items.values().stream()
                .peek(item -> System.out.println("Item: " + item.getMenuItem().getName() + 
                             ", Price: " + item.getMenuItem().getPrice() + 
                             ", Qty: " + item.getQuantity() + 
                             ", Subtotal: " + item.getSubtotal()))
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        System.out.println("Calculated cart total: $" + total);
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public int getItemCount() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void clear() {
        items.clear();
    }

    public static class CartItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final MenuItem menuItem;
        private int quantity;

        public CartItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void incrementQuantity(int amount) {
            this.quantity += amount;
        }

        public BigDecimal getSubtotal() {
            return BigDecimal.valueOf(menuItem.getPrice() * quantity).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
}
