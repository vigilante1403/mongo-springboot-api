package com.aptech.SemesterProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("bookings")
public class Booking {
    @Id
    private String id;
    private String tourId;
    private Tour tour;
    private String userId;
    private User user;
    private String startDate;
    private double priceOrigin;
    private double priceDiscount;
    private double priceFinal;
    private String createdAt;
    private String updatedAt;
    private boolean paid=true;
    private int numPeopleJoined;
    private List<String> names;
    private boolean status;
    private String sessionId;
    private long creationTime;
    private String keysOfStartDate;
    private String startDateId;
}
