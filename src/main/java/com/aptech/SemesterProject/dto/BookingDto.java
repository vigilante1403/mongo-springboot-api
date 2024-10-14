package com.aptech.SemesterProject.dto;

import com.aptech.SemesterProject.entity.Tour;
import com.aptech.SemesterProject.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDto {
    private String id;
    private Tour tour;
    private User user;
    private String startDate;
    private double priceOrigin;
    private double priceDiscount;
    private double priceFinal;
    private int numJoin;
    private boolean paid;
    private String createdAt;
    private boolean status;
    private String sessionId;
    private long creationTime;
    private String keysOfStartDate;
    private String startDateId;

}
