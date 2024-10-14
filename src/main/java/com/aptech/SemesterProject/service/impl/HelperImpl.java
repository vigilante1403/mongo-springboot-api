package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.entity.Tour;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.repo.TourRepo;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class HelperImpl implements Helper {
    private String dateFormat = "yyyy-MM-dd";

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TourRepo tourRepo;

    @Override
    public Map<String, Object> checkUserValid(String userId) {
        User user = userRepo.findById(userId).orElse(null);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", user != null ? true : false);
        if (user != null) {
            result.put("data", user);
        }else{
            result.put("data",null);
        }
        return result;
    }

    @Override
    public Map<String, Object> checkTourValid(String tourId) {
        Tour tour = tourRepo.findById(tourId).orElse(null);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", tour != null ? true : false);
        if (tour != null) {
            result.put("data", tour);
        } else {
            result.put("data", null);
        }
        return result;
    }

    @Override
    public Map<String, Object> checkDateValid(String dateInput) {
        Map<String, Object> result = new HashMap<>();
        DateFormat df = new SimpleDateFormat(this.dateFormat);
        df.setLenient(false);
        try {
            Date date = df.parse(dateInput);
            System.out.println(date);
            result.put("valid", true);
            result.put("data", date);
        } catch (ParseException e) {
            result.put("valid", false);
        }
        return result;
    }

    @Override
    public Map<String, Object> checkUserValidByEmail(String email) {
        User user = null;
        user = userRepo.findByEmail(email).get(0);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", user != null ? true : false);
        if (user != null) result.put("data", user);
        return result;
    }

    @Override
    public String checkIfExistKeyAndRepeat(String random, Map<String, LocalDate> map) {

        String random1=random;
        while(true){
            if(map.containsKey(random1)){
                random1=UUID.randomUUID().toString().substring(0,15);
            }else{
                break;
            }

        }
        return random1;
    }
}
