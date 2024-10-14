package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.dto.ScheduleWorking;
import com.aptech.SemesterProject.exception.CustomRuntimeException;

import java.util.List;

public interface ScheduleService {
    List<ScheduleWorking> getAllSchedulesOfAllGuides();
    List<ScheduleWorking> getSchedulesOfSpecificGuide(String guideId) throws CustomRuntimeException;
    List<ScheduleWorking> getSchedulesOfTour(String tourId);
    ScheduleWorking getDetailsOfASchedule(String scheduleId);
    List<ScheduleWorking> getListOfSchedulesFromASingleBookingId(String bookingId);
}
