package com.aptech.SemesterProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("reviews")
public class Review {
    @Id
    private String id;
    private String review;
    private double rating;
    private String createdAt;
    private String lastUpdate;
    private String tourId;
    private Tour tour;
    private String userId;
    private User user;
    private boolean shown;
    private String bookingId;
}
