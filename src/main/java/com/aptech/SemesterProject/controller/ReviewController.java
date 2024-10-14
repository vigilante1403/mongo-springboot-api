package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.dto.ReviewDto;
import com.aptech.SemesterProject.entity.Review;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto>> getAllReviewsOfUser(@PathVariable String userId) throws CustomRuntimeException {
        return new ResponseEntity<>(reviewService.findReviewsOfUser(userId), HttpStatus.OK);
    }
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ReviewDto>> getAllReviewsOfTour(@PathVariable String tourId) throws CustomRuntimeException {
        return new ResponseEntity<>(reviewService.findReviewsOfTour(tourId), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/add")
    public ResponseEntity<ReviewDto> addNewReview(@RequestParam(name="tour")String tourId,@RequestParam(name="user")String userId,@RequestParam(name="review")String review,@RequestParam(name="rating")double rating,@RequestParam(name="shown")boolean shown,@RequestParam(name="bookingId")String bookingId) throws CustomRuntimeException {
        ReviewDto result=reviewService.addNewReview(tourId,userId,review,rating,shown,bookingId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/update")
    public ResponseEntity<ReviewDto> updateAReview(@RequestParam(name="tour")String tourId,@RequestParam(name="user")String userId,@RequestParam(name="review")String review,@RequestParam(name="rating")double rating,@RequestParam(name="shown")boolean shown,@RequestParam(name="bookingId")String bookingId) throws CustomRuntimeException {
        ReviewDto result=reviewService.updateReview(tourId, userId, review, rating,shown,bookingId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER','LEADGUIDE','GUIDE')")
    @GetMapping("/user/{userId}/tour/{tourId}")
    public ResponseEntity<List<ReviewDto>> getSpecificReviewsOfUser(@PathVariable String userId,@PathVariable String tourId) throws CustomRuntimeException {
        return new ResponseEntity<>(reviewService.getReviewsOfUserAndTour(tourId,userId),HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/delete")
    public ResponseEntity deleteSpecificReview(@RequestParam(name="reviewId") String reviewId){
        System.out.print(reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/delete/tour/{tourId}")
    public ResponseEntity deleteAllReviewsOfSpecificTour(@PathVariable String tourId) throws CustomRuntimeException {
        reviewService.deleteAllReviewsOfTour(tourId);
        return ResponseEntity.noContent().build();
    }

}
