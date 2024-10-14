package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.dto.ReviewDto;
import com.aptech.SemesterProject.entity.Review;
import com.aptech.SemesterProject.exception.CustomRuntimeException;

import java.util.List;

public interface ReviewService {
    ReviewDto addNewReview(String tourId, String userId,String review,double rating,boolean shown,String bookingId) throws CustomRuntimeException;
    ReviewDto updateReview(String tourId, String userId,String review,double rating,boolean shown,String bookingId) throws CustomRuntimeException;
    List<ReviewDto> findReviewsOfUser(String userId) throws CustomRuntimeException;
    List<ReviewDto> findReviewsOfTour(String tourId) throws CustomRuntimeException;
    List<ReviewDto> getReviewsOfUserAndTour(String tourId, String userId) throws CustomRuntimeException;
    void deleteReview(String reviewId);
    void deleteAllReviewsOfTour(String tourId) throws CustomRuntimeException;
}
