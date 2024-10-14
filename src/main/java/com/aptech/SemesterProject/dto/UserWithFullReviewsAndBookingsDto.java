package com.aptech.SemesterProject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserWithFullReviewsAndBookingsDto {
    private String id;
    private String displayName;
    private String email;
    private String photo;
    private String fullName;
    private String nationality;
    private String nationalID;
    private String countryFlag;
    private String role;
    private String createdAt;
    private String lastLoginDate;
    private boolean isActive;
    private boolean isNotLocked;
    private List<TourDto> joinedTours;
    private List<ReviewDto> reviews;
    private List<BookingDto> bookings;

    public UserWithFullReviewsAndBookingsDto(String id, String displayName, String email, String photo, String fullName, String nationality, String nationalID, String countryFlag, String role, String createdAt, String lastLoginDate, boolean isActive, boolean isNotLocked, List<TourDto> joinedTours, List<ReviewDto> reviews, List<BookingDto> bookings) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.photo = photo;
        this.fullName = fullName;
        this.nationality = nationality;
        this.nationalID = nationalID;
        this.countryFlag = countryFlag;
        this.role = role;
        this.createdAt = createdAt;
        this.lastLoginDate = lastLoginDate;
        this.isActive = isActive;
        this.isNotLocked = isNotLocked;
        this.joinedTours = joinedTours;
        this.reviews = reviews;
        this.bookings = bookings;
    }
}
