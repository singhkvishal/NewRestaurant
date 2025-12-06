package com.example.restaurant.controller;

import com.example.restaurant.model.Review;
import com.example.restaurant.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String showReviews(Model model) {
        model.addAttribute("reviews", reviewService.getApprovedReviews());
        model.addAttribute("averageRating", reviewService.getAverageRating());
        return "reviews/reviews";
    }

    @GetMapping("/submit")
    public String showReviewForm(Model model) {
        if (!model.containsAttribute("review")) {
            model.addAttribute("review", new Review());
        }
        return "reviews/submit-review";
    }

    @PostMapping("/submit")
    public String submitReview(@Valid @ModelAttribute("review") Review review,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        // Set the date posted to now
        review.setDatePosted(java.time.LocalDateTime.now());
        if (result.hasErrors()) {
            return "reviews/submit-review";
        }
        
        reviewService.saveReview(review);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you for your review! It will be visible after approval.");
        return "redirect:/reviews";
    }

    // Admin endpoints (in a real app, these should be secured)
    @GetMapping("/admin/pending")
    public String showPendingReviews(Model model) {
        model.addAttribute("pendingReviews", 
            reviewService.getAllReviews().stream()
                .filter(review -> !review.isApproved())
                .toList()
        );
        return "admin/pending-reviews";
    }

    @PostMapping("/admin/approve/{id}")
    public String approveReview(@PathVariable Long id) {
        reviewService.approveReview(id);
        return "redirect:/reviews/admin/pending";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/reviews/admin/pending";
    }
}
