package com.aptech.SemesterProject.service;

import java.time.LocalDate;
import java.util.Map;

public interface Helper {
    Map<String,Object> checkUserValid(String userId);
    Map<String,Object> checkTourValid(String tourId);
    Map<String,Object> checkDateValid(String dateInput);
    Map<String,Object> checkUserValidByEmail(String email);
    String checkIfExistKeyAndRepeat(String random, Map<String, LocalDate> map);
}
