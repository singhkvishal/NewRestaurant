package com.example.restaurant.model;

import java.time.LocalDateTime;

public class Review {
    private Long id;
    private String customerName;
    private String email;
    private int rating;
    private String comment;
    private LocalDateTime datePosted;
    private boolean approved;

    // Constructors
    public Review() {
        this.datePosted = LocalDateTime.now();
        this.approved = false; // Reviews are not approved by default
    }

    public Review(String customerName, String email, int rating, String comment) {
        this.customerName = customerName;
        this.email = email;
        this.rating = rating;
        this.comment = comment;
        this.datePosted = LocalDateTime.now();
        this.approved = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", datePosted=" + datePosted +
                ", approved=" + approved +
                '}';
    }
}
