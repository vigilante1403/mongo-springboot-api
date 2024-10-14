package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.Location;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepo extends MongoRepository<Location, String> {
}
