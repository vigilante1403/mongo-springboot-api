package com.aptech.SemesterProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("startDates")
public class StartDate {
    @Id
    private String id;
    private String tourId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean status;
    private Map<String,Object> locations;

}
