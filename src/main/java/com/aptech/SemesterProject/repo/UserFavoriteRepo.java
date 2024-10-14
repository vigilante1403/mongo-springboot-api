package com.aptech.SemesterProject.repo;

import com.aptech.SemesterProject.entity.UserFavorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserFavoriteRepo extends MongoRepository<UserFavorite,String> {
    List<UserFavorite> findByUserId(String userId);
    List<UserFavorite> findByTourId(String tourId);

}
