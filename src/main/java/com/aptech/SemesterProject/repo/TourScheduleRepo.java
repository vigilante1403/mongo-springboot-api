package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.TourSchedule;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TourScheduleRepo extends MongoRepository<TourSchedule,String> {
    List<TourSchedule> findByTourId(String tourId);
    List<TourSchedule> findByStartDateId(String startDate);
    List<TourSchedule> findByGuideId(String guideId);
}
