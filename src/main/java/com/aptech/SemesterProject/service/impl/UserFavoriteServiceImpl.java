package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.entity.UserFavorite;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.UserFavoriteRepo;
import com.aptech.SemesterProject.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {
    @Autowired
    private UserFavoriteRepo userFavoriteRepo;
    @Override
    public List<UserFavorite> getListFavoriteItemsOfUser(String userId) {
        List<UserFavorite> list = userFavoriteRepo.findByUserId(userId);
        return list;
    }

    @Override
    public List<UserFavorite> getListOfTotalUsersTowardsATour(String tourId) {
        List<UserFavorite> list = userFavoriteRepo.findByTourId(tourId);
        return list;
    }

    @Override
    public UserFavorite addNewFavoriteOfUserWithTour(String userId, String tourId) {
        List<UserFavorite> list1 = userFavoriteRepo.findByUserId(userId);
        if(!list1.isEmpty()){
            UserFavorite userFavorite = list1.stream().filter(userFavorite1 -> userFavorite1.getTourId().equals(tourId)).findFirst().orElse(null);
            if(userFavorite!=null) {
                userFavorite.setLiked(true);
                UserFavorite result=userFavoriteRepo.save(userFavorite);
                return result;
            }
        }
        UserFavorite newFavorite = new UserFavorite();
        newFavorite.setTourId(tourId);
        newFavorite.setUserId(userId);
        newFavorite.setLiked(true);
        UserFavorite result1 = userFavoriteRepo.save(newFavorite);
        return result1;
    }

    @Override
    public UserFavorite updateFavoriteOfUserWithTour(String userId, String tourId, boolean liked) {
        List<UserFavorite> list = userFavoriteRepo.findByUserId(userId);
        UserFavorite userFavorite = list.stream().filter(userFavorite1 -> userFavorite1.getTourId().equals(tourId)).findFirst().orElse(null);
        if(userFavorite!=null){
            userFavorite.setLiked(liked);
            UserFavorite result = userFavoriteRepo.save(userFavorite);
            return result;
        }
        UserFavorite newFavorite = new UserFavorite();
        newFavorite.setTourId(tourId);
        newFavorite.setUserId(userId);
        newFavorite.setLiked(liked);
        UserFavorite result1 = userFavoriteRepo.save(newFavorite);
        return result1;
    }

    @Override
    public void makeAllFavoritesOfUserToFalse(String userId) {
        List<UserFavorite> list = userFavoriteRepo.findByUserId(userId);
        if(!list.isEmpty()){
            list.stream().forEach(userFavorite -> userFavorite.setLiked(false));
            userFavoriteRepo.saveAll(list);
        }
    }
}
