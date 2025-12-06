package com.example.restaurant.controller;

import com.example.restaurant.model.MenuItem;
import com.example.restaurant.service.CartService;
import com.example.restaurant.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
}
