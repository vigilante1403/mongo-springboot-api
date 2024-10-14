package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepo extends MongoRepository<User,String> {
    List<User> findByEmail(String email);
}
