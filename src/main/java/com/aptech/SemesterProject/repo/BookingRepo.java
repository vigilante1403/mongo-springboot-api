package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepo extends MongoRepository<Booking,String> {
    List<Booking> findByUserId(String user);
    List<Booking> findByTourId(String tour);
    void deleteByTour(String tour);
    void deleteByUser(String user);
    List<Booking> findBySessionId(String sessionId);
    List<Booking> findByKeysOfStartDate(String keysOfStartDate);
    List<Booking> findByStartDateId(String startId);
}
