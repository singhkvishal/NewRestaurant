package com.example.restaurant.controller;

import com.example.restaurant.service.CartService;
import com.example.restaurant.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final MenuService menuService;

    public CartController(CartService cartService, MenuService menuService) {
        this.cartService = cartService;
        this.menuService = menuService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("cartTotal", cartService.getTotal());
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long menuItemId, 
                           @RequestParam(defaultValue = "1") int quantity) {
        menuService.getMenuItemById(menuItemId).ifPresent(
            menuItem -> cartService.addItem(menuItem, quantity)
        );
        return "redirect:/menu/item/" + menuItemId + "?addedToCart";
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long menuItemId, 
                                @RequestParam int quantity) {
        cartService.updateQuantity(menuItemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long menuItemId) {
        cartService.removeItem(menuItemId);
        return "redirect:/cart";
    }

    /**
     * Synchronize the session cart with a full set of item_* quantities.
     * This is used by the /order/menu page to keep the cart in sync via AJAX.
     */
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<Void> syncCart(@RequestParam Map<String, String> allParams) {
        // Clear existing cart
        cartService.clear();

        // Rebuild from incoming quantities
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("item_")) {
                Long itemId = Long.parseLong(key.substring(5));
                int quantity = Integer.parseInt(entry.getValue());
                if (quantity > 0) {
                    menuService.getMenuItemById(itemId).ifPresent(menuItem ->
                        cartService.addItem(menuItem, quantity)
                    );
                }
            }
        }

        return ResponseEntity.ok().build();
    }
}
