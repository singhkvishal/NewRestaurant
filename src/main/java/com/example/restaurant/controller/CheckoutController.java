package com.example.restaurant.controller;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.MenuItem;
import com.example.restaurant.repository.MenuItemRepository;
import com.example.restaurant.service.CartService;
import com.example.restaurant.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final CartService cartService;
    private final OrderService orderService;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService, MenuItemRepository menuItemRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    public String showCheckoutForm(Model model) {
        List<CartService.CartItem> items = cartService.getItems();
        BigDecimal total = cartService.getTotal();
        
        // Debug logging
        System.out.println("Cart items in checkout: " + items);
        System.out.println("Cart total in checkout: " + total);
        
        if (items.isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", total);
        model.addAttribute("order", new Order());
        return "checkout/checkout";
    }

    @PostMapping
    public String processCheckout(@ModelAttribute Order order, RedirectAttributes redirectAttributes, Model model) {
        if (cartService.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items to your cart before checkout.");
            return "redirect:/cart";
        }
        
        // First, check for any unavailable items in the cart
        List<Long> unavailableItemIds = new ArrayList<>();
        for (CartService.CartItem cartItem : cartService.getItems()) {
            if (!menuItemRepository.existsById(cartItem.getMenuItem().getId())) {
                unavailableItemIds.add(cartItem.getMenuItem().getId());
            }
        }
        
        // If there are unavailable items, remove them from the cart and show a warning
        if (!unavailableItemIds.isEmpty()) {
            for (Long itemId : unavailableItemIds) {
                cartService.removeItem(itemId);
            }
            redirectAttributes.addFlashAttribute("warningMessage", 
                "Some items in your cart were no longer available and have been removed. Please review your order and try again.");
            return "redirect:/checkout";
        }
        
        try {
            // Place the order
            Order savedOrder = placeOrder(order);
            
            // Clear the cart
            cartService.clear();
            
            redirectAttributes.addFlashAttribute("orderId", savedOrder.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Your order has been placed successfully!");
            return "redirect:/order/confirmation/" + savedOrder.getId();
            
        } catch (IllegalArgumentException e) {
            // Handle missing menu items or other validation errors
            model.addAttribute("cartItems", cartService.getItems());
            model.addAttribute("cartTotal", cartService.getTotal());
            model.addAttribute("errorMessage", e.getMessage());
            return "checkout/checkout";
        } catch (Exception e) {
            model.addAttribute("cartItems", cartService.getItems());
            model.addAttribute("cartTotal", cartService.getTotal());
            model.addAttribute("errorMessage", "An error occurred while processing your order. Please try again.");
            return "checkout/checkout";
        }
    }
    
    /**
     * Places an order with the current cart items
     * @param order The order details
     * @return The saved order with generated ID
     */
    private Order placeOrder(Order order) {
        if (cartService.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }
        
        // Convert cart items to the required Map<Long, Integer> format (itemId -> quantity)
        Map<Long, Integer> itemsWithQuantities = cartService.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getMenuItem().getId(),
                        CartService.CartItem::getQuantity,
                        (existing, replacement) -> existing + replacement, // handle duplicate keys by summing quantities
                        HashMap::new // use HashMap for better performance
                ));
        
        // Calculate and set the total amount
        double total = cartService.getTotal().doubleValue();
        order.setTotalAmount(total);
        
        // Save and return the order
        return orderService.createOrder(order, itemsWithQuantities);
    }
}
