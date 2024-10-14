package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.Discount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DiscountRepo extends MongoRepository<Discount,String> {
    List<Discount> findByTourId(String tourId);

}
