package com.aptech.SemesterProject.dto;

import com.aptech.SemesterProject.entity.Booking;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BillDto {
    private String id;
    private Booking booking;
    private LocalDate paidAt;

}