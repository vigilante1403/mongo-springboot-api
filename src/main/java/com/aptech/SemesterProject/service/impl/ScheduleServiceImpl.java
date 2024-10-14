package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.constant.RoleEnum;
import com.aptech.SemesterProject.dto.Guest;
import com.aptech.SemesterProject.dto.ScheduleWorking;
import com.aptech.SemesterProject.entity.*;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.*;
import com.aptech.SemesterProject.service.ScheduleService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private TourScheduleRepo tourScheduleRepo;
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private TourRepo tourRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private StartDateRepo startDateRepo;

    @Override
    public List<ScheduleWorking> getAllSchedulesOfAllGuides() {
        List<TourSchedule> allSchedules = tourScheduleRepo.findAll();
        List<Booking> bookingList = bookingRepo.findAll();

        List<ScheduleWorking> workingList = new ArrayList<>();
        allSchedules.stream().forEach(schedule->{
            ScheduleWorking working = new ScheduleWorking();
            working.setId(schedule.getId());
            working.setGuideId(schedule.getGuideId());
            working.setTourId(schedule.getTourId());
            working.setStartDateId(schedule.getStartDateId());
            StartDate date = startDateRepo.findById(schedule.getStartDateId()).orElse(null);
            if(date!=null){
                if(date.getLocations()!=null&&!date.getLocations().isEmpty()){
                    working.setLocations(date.getLocations());
                }
                working.setStatus(date.isStatus());
                if(date.getStartDate().isBefore(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("completed");
                }else if(date.getStartDate().isEqual(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("ongoing");
                } else if(date.getStartDate().isAfter(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("upcoming");
                }else{
                    working.setTourStatus("canceled");
                }
            }
            working.setFrom(schedule.getStartDate());
            working.setTo(schedule.getEndDate());
            Tour tour = tourRepo.findById(schedule.getTourId()).orElse(null);
            if(tour==null){
                working.setTourName("Deleted Tour");
            }else{
                working.setTourName(tour.getName());
                working.setCountryName(tour.getCountryNameCommon());
                working.setCountryFlag(tour.getCountryFlag());
            }
            User guide = userRepo.findById(schedule.getGuideId()).orElse(null);
            if(guide==null||guide.getRole().equals(RoleEnum.USER)){
                working.setGuideName("Unidentified guide name");
                working.setGuideEmail("Unidentified guide email");
            }else{
                String nameDisplay = guide.getFullName()==null?guide.getName():guide.getFullName();
                working.setGuideEmail(guide.getEmail());
                working.setGuideName(nameDisplay);
                working.setGuideRole(guide.getRole().toString());

            }
            List<Guest> guestList;
            List<Booking> bookings = bookingList.stream().filter(booking -> booking.getTourId().equals(schedule.getTourId())&&booking.getStartDateId().equals(schedule.getStartDateId())).collect(Collectors.toList());
            guestList = bookings.stream().map(booking -> new Guest(booking.getId(),booking.getUserId(),booking.getUser().getEmail(),booking.getUser().getFullName(),booking.getNumPeopleJoined()!=0?booking.getNumPeopleJoined():1)).collect(Collectors.toList());
            working.setGuestList(guestList);
            workingList.add(working);
        });

        return workingList.stream().sorted((w1, w2) -> w2.getTo().compareTo(w1.getTo())).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleWorking> getSchedulesOfSpecificGuide(String guideId) throws CustomRuntimeException {
        User guide = userRepo.findById(guideId).orElse(null);
        if(guide!=null&&(guide.getRole().equals(RoleEnum.USER)||guide.getRole().equals(RoleEnum.ADMIN))){
            throw new CustomRuntimeException("This id isn't belong to a guide");
        }
        List<TourSchedule> allSchedules = tourScheduleRepo.findByGuideId(guideId);
        if(allSchedules.isEmpty()) return null;
        List<Booking> bookingList = bookingRepo.findAll();
        List<ScheduleWorking> workingList = new ArrayList<>();
        allSchedules.stream().forEach(schedule->{
            ScheduleWorking working = new ScheduleWorking();
            working.setId(schedule.getId());
            working.setGuideId(schedule.getGuideId());
            working.setTourId(schedule.getTourId());
            working.setStartDateId(schedule.getStartDateId());
            StartDate date = startDateRepo.findById(schedule.getStartDateId()).orElse(null);
            if(date!=null){
                if(date.getLocations()!=null&&!date.getLocations().isEmpty()){
                    working.setLocations(date.getLocations());
                }
                working.setStatus(date.isStatus());

                if(date.getStartDate().isBefore(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("completed");
                }else if(date.getStartDate().isEqual(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("ongoing");
                } else if(date.getStartDate().isAfter(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("upcoming");
                }else{
                    working.setTourStatus("canceled");
                }
            }
            working.setFrom(schedule.getStartDate());
            working.setTo(schedule.getEndDate());
            Tour tour = tourRepo.findById(schedule.getTourId()).orElse(null);
            if(tour==null){
                working.setTourName("Deleted Tour");
            }else{
                working.setTourName(tour.getName());
                working.setCountryName(tour.getCountryNameCommon());
                working.setCountryFlag(tour.getCountryFlag());
            }

            if(guide==null){
                working.setGuideName("Unidentified guide name");
                working.setGuideEmail("Unidentified guide email");
            }else{
                String nameDisplay = guide.getFullName()==null?guide.getName():guide.getFullName();
                working.setGuideEmail(guide.getEmail());
                working.setGuideName(nameDisplay);
                working.setGuideRole(guide.getRole().toString());

            }
            List<Guest> guestList;
            List<Booking> bookings = bookingList.stream().filter(booking -> booking.getTourId().equals(schedule.getTourId())&&booking.getStartDateId().equals(schedule.getStartDateId())).collect(Collectors.toList());
            guestList = bookings.stream().map(booking -> new Guest(booking.getId(),booking.getUserId(),booking.getUser().getEmail(),booking.getUser().getFullName(),booking.getNumPeopleJoined()!=0?booking.getNumPeopleJoined():1)).collect(Collectors.toList());
            working.setGuestList(guestList);
            workingList.add(working);
        });
        return workingList.stream().sorted((w1, w2) -> w2.getTo().compareTo(w1.getTo())).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleWorking> getSchedulesOfTour(String tourId) {
        List<TourSchedule> allSchedules = tourScheduleRepo.findByTourId(tourId);
        List<Booking> bookingList = bookingRepo.findAll();
        List<ScheduleWorking> workingList = new ArrayList<>();
        allSchedules.stream().forEach(schedule->{
            ScheduleWorking working = new ScheduleWorking();
            working.setId(schedule.getId());
            working.setGuideId(schedule.getGuideId());
            working.setTourId(schedule.getTourId());
            working.setStartDateId(schedule.getStartDateId());
            StartDate date = startDateRepo.findById(schedule.getStartDateId()).orElse(null);
            if(date!=null){
                if(date.getLocations()!=null&&!date.getLocations().isEmpty()){
                    working.setLocations(date.getLocations());
                }
                working.setStatus(date.isStatus());
                if(date.getStartDate().isBefore(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("completed");
                }else if(date.getStartDate().isEqual(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("ongoing");
                } else if(date.getStartDate().isAfter(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("upcoming");
                }else{
                    working.setTourStatus("canceled");
                }
            }
            working.setFrom(schedule.getStartDate());
            working.setTo(schedule.getEndDate());
            Tour tour = tourRepo.findById(schedule.getTourId()).orElse(null);
            if(tour==null){
                working.setTourName("Deleted Tour");
            }else{
                working.setTourName(tour.getName());
                working.setCountryName(tour.getCountryNameCommon());
                working.setCountryFlag(tour.getCountryFlag());
            }
            User guide = userRepo.findById(schedule.getGuideId()).orElse(null);
            if(guide==null||guide.getRole().equals(RoleEnum.USER)){
                working.setGuideName("Unidentified guide name");
                working.setGuideEmail("Unidentified guide email");
            }else{
                String nameDisplay = guide.getFullName()==null?guide.getName():guide.getFullName();
                working.setGuideEmail(guide.getEmail());
                working.setGuideName(nameDisplay);
                working.setGuideRole(guide.getRole().toString());

            }
            List<Guest> guestList;
            List<Booking> bookings = bookingList.stream().filter(booking -> booking.getTourId().equals(schedule.getTourId())&&booking.getStartDateId().equals(schedule.getStartDateId())).collect(Collectors.toList());
            guestList = bookings.stream().map(booking -> new Guest(booking.getId(),booking.getUserId(),booking.getUser().getEmail(),booking.getUser().getFullName(),booking.getNumPeopleJoined()!=0?booking.getNumPeopleJoined():1)).collect(Collectors.toList());
            working.setGuestList(guestList);
            workingList.add(working);
        });

        return workingList.stream().sorted((w1, w2) -> w2.getTo().compareTo(w1.getTo())).collect(Collectors.toList());
    }

    @Override
    public ScheduleWorking getDetailsOfASchedule(String scheduleId) {
        TourSchedule schedule = tourScheduleRepo.findById(scheduleId).orElse(null);
        if(schedule==null) return null;
        List<Booking> bookingList = bookingRepo.findAll();
            ScheduleWorking working = new ScheduleWorking();
            working.setId(schedule.getId());
            working.setGuideId(schedule.getGuideId());
            working.setTourId(schedule.getTourId());
            working.setStartDateId(schedule.getStartDateId());
            working.setFrom(schedule.getStartDate());
            working.setTo(schedule.getEndDate());
        StartDate date = startDateRepo.findById(schedule.getStartDateId()).orElse(null);
        if(date!=null){
            if(date.getLocations()!=null&&!date.getLocations().isEmpty()){
                working.setLocations(date.getLocations());
            }
            working.setStatus(date.isStatus());
            if(date.getStartDate().isBefore(LocalDate.now())&&date.isStatus()){
                working.setTourStatus("completed");
            }else if(date.getStartDate().isEqual(LocalDate.now())&&date.isStatus()){
                working.setTourStatus("ongoing");
            } else if(date.getStartDate().isAfter(LocalDate.now())&&date.isStatus()){
                working.setTourStatus("upcoming");
            }else{
                working.setTourStatus("canceled");
            }
        }
            Tour tour = tourRepo.findById(schedule.getTourId()).orElse(null);
            if(tour==null){
                working.setTourName("Deleted Tour");
            }else{
                working.setTourName(tour.getName());
                working.setCountryName(tour.getCountryNameCommon());
                working.setCountryFlag(tour.getCountryFlag());
            }
            User guide = userRepo.findById(schedule.getGuideId()).orElse(null);
            if(guide==null||guide.getRole().equals(RoleEnum.USER)){
                working.setGuideName("Unidentified guide name");
                working.setGuideEmail("Unidentified guide email");
            }else{
                String nameDisplay = guide.getFullName()==null?guide.getName():guide.getFullName();
                working.setGuideEmail(guide.getEmail());
                working.setGuideName(nameDisplay);
                working.setGuideRole(guide.getRole().toString());

            }
            List<Guest> guestList;
            List<Booking> bookings = bookingList.stream().filter(booking -> booking.getTourId().equals(schedule.getTourId())&&booking.getStartDateId().equals(schedule.getStartDateId())).collect(Collectors.toList());
            guestList = bookings.stream().map(booking -> new Guest(booking.getId(),booking.getUserId(),booking.getUser().getEmail(),booking.getUser().getFullName(),booking.getNumPeopleJoined()!=0?booking.getNumPeopleJoined():1)).collect(Collectors.toList());
            working.setGuestList(guestList);


        return working;
    }

    @Override
    public List<ScheduleWorking> getListOfSchedulesFromASingleBookingId(String bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElse(null);
        if(b==null) return null;
        List<TourSchedule> schedules = tourScheduleRepo.findByStartDateId(b.getStartDateId());
        List<Booking> bookingList = bookingRepo.findAll();
        List<ScheduleWorking> workingList = new ArrayList<>();
        if(schedules.isEmpty()) return null;
        schedules.stream().forEach(schedule->{
            ScheduleWorking working = new ScheduleWorking();
            working.setId(schedule.getId());
            working.setGuideId(schedule.getGuideId());
            working.setTourId(schedule.getTourId());
            working.setStartDateId(schedule.getStartDateId());
            StartDate date = startDateRepo.findById(schedule.getStartDateId()).orElse(null);
            if(date!=null){
                if(date.getLocations()!=null&&!date.getLocations().isEmpty()){
                    working.setLocations(date.getLocations());
                }
                working.setStatus(date.isStatus());
                if(date.getStartDate().isBefore(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("completed");
                }else if(date.getStartDate().isEqual(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("ongoing");
                } else if(date.getStartDate().isAfter(LocalDate.now())&&date.isStatus()){
                    working.setTourStatus("upcoming");
                }else{
                    working.setTourStatus("canceled");
                }
            }
            working.setFrom(schedule.getStartDate());
            working.setTo(schedule.getEndDate());
            Tour tour = tourRepo.findById(schedule.getTourId()).orElse(null);
            if(tour==null){
                working.setTourName("Deleted Tour");
            }else{
                working.setTourName(tour.getName());
                working.setCountryName(tour.getCountryNameCommon());
                working.setCountryFlag(tour.getCountryFlag());
            }
            User guide = userRepo.findById(schedule.getGuideId()).orElse(null);
            if(guide==null||guide.getRole().equals(RoleEnum.USER)){
                working.setGuideName("Unidentified guide name");
                working.setGuideEmail("Unidentified guide email");
            }else{
                String nameDisplay = guide.getFullName()==null?guide.getName():guide.getFullName();
                working.setGuideEmail(guide.getEmail());
                working.setGuideName(nameDisplay);
                working.setGuideRole(guide.getRole().toString());

            }
            List<Guest> guestList;
            List<Booking> bookings = bookingList.stream().filter(booking -> booking.getTourId().equals(schedule.getTourId())&&booking.getStartDateId().equals(schedule.getStartDateId())).collect(Collectors.toList());
            guestList = bookings.stream().map(booking -> new Guest(booking.getId(),booking.getUserId(),booking.getUser().getEmail(),booking.getUser().getFullName(),booking.getNumPeopleJoined()!=0?booking.getNumPeopleJoined():1)).collect(Collectors.toList());
            working.setGuestList(guestList);
            workingList.add(working);
        });

        return workingList.stream().sorted((w1, w2) -> w2.getTo().compareTo(w1.getTo())).collect(Collectors.toList());

    }
}
