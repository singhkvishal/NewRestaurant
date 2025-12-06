package com.example.restaurant.controller;

import com.example.restaurant.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam Double amount, Model model) {
        try {
            String clientSecret = paymentService.createPaymentIntent(
                amount, 
                "usd", 
                "Restaurant Reservation Deposit"
            );
            
            model.addAttribute("clientSecret", clientSecret);
            model.addAttribute("stripePublicKey", stripePublicKey);
            model.addAttribute("amount", amount);
            return "payment/checkout";
        } catch (Exception e) {
            model.addAttribute("error", "Error processing payment: " + e.getMessage());
            return "error";
        }
    }
}
