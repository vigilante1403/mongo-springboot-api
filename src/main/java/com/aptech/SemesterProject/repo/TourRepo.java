package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.Tour;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TourRepo extends MongoRepository<Tour,String> {
    List<Tour> findByName(String name);
    Tour findTourBySlug(String slug);
}
