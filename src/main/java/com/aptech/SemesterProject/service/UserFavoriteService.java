package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.entity.UserFavorite;

import java.util.List;

public interface UserFavoriteService {
    List<UserFavorite> getListFavoriteItemsOfUser(String userId);
    List<UserFavorite> getListOfTotalUsersTowardsATour(String tourId);
    UserFavorite addNewFavoriteOfUserWithTour(String userId,String tourId);
    UserFavorite updateFavoriteOfUserWithTour(String userId,String tourId,boolean liked);
    void makeAllFavoritesOfUserToFalse(String userId);
}
