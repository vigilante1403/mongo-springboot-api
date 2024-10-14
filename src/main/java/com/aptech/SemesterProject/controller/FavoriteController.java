package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.entity.UserFavorite;
import com.aptech.SemesterProject.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {
    @Autowired
    private UserFavoriteService userFavoriteService;
    @GetMapping("/user")
    public ResponseEntity<List<UserFavorite>> getFavoriteListOfUser(@RequestParam(name="userId")String userId){
        return new ResponseEntity<>(userFavoriteService.getListFavoriteItemsOfUser(userId), HttpStatus.OK);

    }
    @GetMapping("/tour")
    public ResponseEntity<List<UserFavorite>> getTotalFavoritesAboutTour(@RequestParam(name="tourId")String tourId){
        return new ResponseEntity<>(userFavoriteService.getListOfTotalUsersTowardsATour(tourId),HttpStatus.OK);
    }
    @PostMapping("/user/add")
    public ResponseEntity<UserFavorite> addUserFavorite(@RequestParam(name="userId")String userId,@RequestParam(name="tourId")String tourId){
        UserFavorite result = userFavoriteService.addNewFavoriteOfUserWithTour(userId, tourId);
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }
    @PutMapping("/user/change")
    public ResponseEntity<UserFavorite> alterUserFavorite(@RequestParam(name="userId")String userId,@RequestParam(name="tourId")String tourId,@RequestParam(name="liked") boolean liked){
        UserFavorite result = userFavoriteService.updateFavoriteOfUserWithTour(userId,tourId,liked);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @DeleteMapping("/user/remove-all")
    public ResponseEntity removeAllUserFavorites(@RequestParam(name="userId")String userId){
        userFavoriteService.makeAllFavoritesOfUserToFalse(userId);
        return new ResponseEntity("ok",HttpStatus.NO_CONTENT);
    }
}
