package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillRepo extends MongoRepository<Bill, String> {
    List<Bill> findByUserEmail(String userEmail);
    List<Bill> findByBookingId(String bookingId);

}