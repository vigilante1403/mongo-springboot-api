package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.AuthorizedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuthorizedTokenRepo extends MongoRepository<AuthorizedToken,String> {
    List<AuthorizedToken> findByUserId(String userId);
}
