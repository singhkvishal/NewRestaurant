package com.example.restaurant.service;

import com.example.restaurant.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    List<Review> getApprovedReviews();
    Optional<Review> getReviewById(Long id);
    Review saveReview(Review review);
    void deleteReview(Long id);
    void approveReview(Long id);
    double getAverageRating();
}
