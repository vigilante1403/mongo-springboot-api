package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepo extends MongoRepository<Review,String> {
    List<Review> findByTourId(String tourId);
    List<Review> findByUserId(String userId);
}
