package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.StartDate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface StartDateRepo extends MongoRepository<StartDate,String> {
    List<StartDate> findByStartDate(LocalDate startDate);
    List<StartDate> findByTourId(String tourId);
}
