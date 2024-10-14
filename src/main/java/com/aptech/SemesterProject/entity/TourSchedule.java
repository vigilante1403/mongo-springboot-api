package com.aptech.SemesterProject.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
@Document("schedules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourSchedule {
    @Id
    private String id;
    private String tourId;
    private LocalDate startDate;
    private LocalDate endDate;//+so day
    private String guideId;
//    private long startDateByMillisEpoch;//milisecond
//    private long endDateByMillisEpoch;//at
    private String keyOfDateTour;
    private String startDateId;


}
