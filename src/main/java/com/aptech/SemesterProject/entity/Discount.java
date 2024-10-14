package com.aptech.SemesterProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("discounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    @Id
    private String id;
    private String tourId;
    private double percentageDiscount;
    private LocalDate discountFrom;
    private LocalDate discountTo;
}
