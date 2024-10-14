package com.aptech.SemesterProject.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleWorking {
    private String id;
    private String guideId;
    private String tourId;
    private String tourName;
    private String guideName;
    private String guideRole;
    private String guideEmail;
    private String startDateId;
    private LocalDate from;
    private LocalDate to;
    private String countryFlag;
    private String countryName;
    private List<Guest> guestList;
    private boolean status=true;
    private String tourStatus;
    private Map<String,Object> locations;

}
