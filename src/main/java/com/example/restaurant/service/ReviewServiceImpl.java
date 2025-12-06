package com.example.restaurant.service;

import com.example.restaurant.model.Review;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    // In-memory storage (replace with database in production)
    private final Map<Long, Review> reviews = new HashMap<>();
    private Long nextId = 1L;
    
    public ReviewServiceImpl() {
        // Initialize with some sample reviews
        Review review1 = new Review("John Doe", "john@example.com", 5, "Amazing food and excellent service! Will definitely come back!");
        review1.setId(nextId++);
        review1.setApproved(true);
        reviews.put(review1.getId(), review1);
        
        Review review2 = new Review("Jane Smith", "jane@example.com", 4, "Great atmosphere and delicious food. The service was a bit slow but friendly.");
        review2.setId(nextId++);
        review2.setApproved(true);
        reviews.put(review2.getId(), review2);
    }

    @Override
    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews.values());
    }

    @Override
    public List<Review> getApprovedReviews() {
        return reviews.values().stream()
                .filter(Review::isApproved)
                .sorted((r1, r2) -> {
                    LocalDateTime date1 = r1.getDatePosted() != null ? r1.getDatePosted() : LocalDateTime.MIN;
                    LocalDateTime date2 = r2.getDatePosted() != null ? r2.getDatePosted() : LocalDateTime.MIN;
                    return date2.compareTo(date1); // Sort in descending order (newest first)
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public Review saveReview(Review review) {
        if (review.getId() == null) {
            review.setId(nextId++);
            review.setDatePosted(LocalDateTime.now());
        }
        reviews.put(review.getId(), review);
        return review;
    }

    @Override
    public void deleteReview(Long id) {
        reviews.remove(id);
    }

    @Override
    public void approveReview(Long id) {
        getReviewById(id).ifPresent(review -> {
            review.setApproved(true);
            reviews.put(id, review);
        });
    }

    @Override
    public double getAverageRating() {
        try {
            List<Review> approvedReviews = getApprovedReviews();
            if (approvedReviews == null || approvedReviews.isEmpty()) {
                return 0.0;
            }
            return approvedReviews.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
