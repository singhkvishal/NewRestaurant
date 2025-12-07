package com.example.restaurant.controller;

import com.example.restaurant.model.MenuItem;
import com.example.restaurant.model.Order;
import com.example.restaurant.service.MenuService;
import com.example.restaurant.service.OrderService;
import com.example.restaurant.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final MenuService menuService;
    private final CartService cartService;

    @Autowired
    public OrderController(OrderService orderService, MenuService menuService, CartService cartService) {
        this.orderService = orderService;
        this.menuService = menuService;
        this.cartService = cartService;
    }

    @GetMapping("/menu")
    public String showMenu(Model model) {
        List<MenuItem> menuItems = menuService.getAllMenuItems();
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("cart", new HashMap<Long, Integer>());
        return "order/menu";
    }

    @PostMapping("/checkout")
    public String showCheckout(@RequestParam Map<String, String> allParams, Model model) {
        Map<Long, Integer> cart = new HashMap<>();
        final double[] totalWrapper = {0.0};
        
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("item_")) {
                Long itemId = Long.parseLong(entry.getKey().substring(5));
                int quantity = Integer.parseInt(entry.getValue());
                if (quantity > 0) {
                    cart.put(itemId, quantity);
                    // Calculate total price (this is just an estimate, actual calculation should be done in service)
                    menuService.getMenuItemById(itemId).ifPresent(item -> {
                        totalWrapper[0] += item.getPrice() * quantity;
                    });
                }
            }
        }
        
        double total = totalWrapper[0];
        
        if (cart.isEmpty()) {
            return "redirect:/order/menu?error=empty_cart";
        }
        
        model.addAttribute("cartItems", menuService.getMenuItemsByIds(
            cart.keySet().stream()
                .map(key -> Long.parseLong(key.toString()))
                .collect(Collectors.toSet())
        ));
        model.addAttribute("quantities", cart);
        model.addAttribute("totalAmount", total);
        model.addAttribute("order", new Order());
        
        return "order/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(Order order, @RequestParam Map<String, String> allParams, Principal principal) {
        Map<Long, Integer> itemsWithQuantities = new HashMap<>();
        
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("item_")) {
                Long itemId = Long.parseLong(entry.getKey().substring(5));
                int quantity = Integer.parseInt(entry.getValue());
                if (quantity > 0) {
                    itemsWithQuantities.put(itemId, quantity);
                }
            }
        }
        
        if (itemsWithQuantities.isEmpty()) {
            return "redirect:/order/menu?error=empty_cart";
        }
        
        // Set customer email if user is logged in
        if (principal != null) {
            order.setEmail(principal.getName());
        }
        
        Order savedOrder = orderService.createOrder(order, itemsWithQuantities);
        return "redirect:/order/confirmation/" + savedOrder.getId();
    }

    /**
     * Sync items selected on the /order/menu page into the session CartService
     * and then redirect to the unified checkout flow at /checkout.
     */
    @PostMapping("/sync-cart")
    public String syncCart(@RequestParam Map<String, String> allParams) {
        // Clear existing cart to reflect current selection
        cartService.clear();

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

        if (cartService.getItems().isEmpty()) {
            return "redirect:/order/menu?error=empty_cart";
        }

        return "redirect:/checkout";
    }

    @GetMapping("/confirmation/{orderId}")
    public String showConfirmation(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order/confirmation";
    }

    @GetMapping("/track/{orderId}")
    public String trackOrder(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order/track";
    }
}
