package com.aptech.SemesterProject.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDto {
    private String id;
    private String review;
    private double rating;
    private String userName;
    private String tourName;
    private String createdAt;
    private String updatedAt;
    private String tourId;
    private boolean shown;
    private String tourImageCover;
    private String userId;
    private String travelDate;
    private String bookingId;
}
