package com.example.restaurant.service;

import com.example.restaurant.model.MenuItem;
import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderItem;
import com.example.restaurant.repository.MenuItemRepository;
import com.example.restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public Order createOrder(Order order, Map<Long, Integer> itemsWithQuantities) {
        if (itemsWithQuantities == null || itemsWithQuantities.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order with empty items");
        }
        
        // Set order details
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        // Check all items exist before processing
        List<String> unavailableItems = new ArrayList<>();
        Map<Long, MenuItem> availableItems = new HashMap<>();
        
        for (Long menuItemId : itemsWithQuantities.keySet()) {
            Optional<MenuItem> menuItemOpt = menuItemRepository.findById(menuItemId);
            if (menuItemOpt.isEmpty()) {
                unavailableItems.add("ID: " + menuItemId);
            } else {
                MenuItem item = menuItemOpt.get();
                availableItems.put(menuItemId, item);
                // If you want to add availability check in the future, you can add a boolean 'available' field to MenuItem
                // and uncomment the following lines:
                // if (!item.isAvailable()) {
                //     unavailableItems.add(String.format("%s (ID: %d)", item.getName(), item.getId()));
                // } else {
                //     availableItems.put(menuItemId, item);
                // }
            }
        }
        
        if (!unavailableItems.isEmpty()) {
            throw new IllegalArgumentException("The following menu items are no longer available: " + 
                String.join(", ", unavailableItems) + 
                ". Please remove them from your order and try again.");
        }
        
        // Calculate total amount and create order items
        double totalAmount = 0.0;
        
        for (Map.Entry<Long, Integer> entry : itemsWithQuantities.entrySet()) {
            MenuItem menuItem = availableItems.get(entry.getKey());
            Integer quantity = entry.getValue();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(menuItem.getPrice());
            
            order.addItem(orderItem);
            totalAmount += orderItem.getItemTotal();
        }
        
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void updatePaymentStatus(String paymentIntentId, String status) {
        // In a real application, you would implement this to update payment status
        // This is a placeholder implementation
        orderRepository.findByPaymentIntentId(paymentIntentId).ifPresent(order -> {
            order.setPaymentStatus(status);
            orderRepository.save(order);
        });
    }

    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByEmailOrderByOrderTimeDesc(email);
    }
}
