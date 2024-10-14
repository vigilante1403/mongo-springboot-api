package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.dto.ReviewDto;
import com.aptech.SemesterProject.entity.Booking;
import com.aptech.SemesterProject.entity.Review;
import com.aptech.SemesterProject.entity.Tour;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.BookingRepo;
import com.aptech.SemesterProject.repo.ReviewRepo;
import com.aptech.SemesterProject.repo.TourRepo;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.Helper;
import com.aptech.SemesterProject.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TourRepo tourRepo;
    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private Helper helper;
    @Autowired
    private BookingRepo bookingRepo;

    @Override
    public ReviewDto addNewReview(String tourId, String userId, String review, double rating, boolean shown, String bookingId) throws CustomRuntimeException {
        List<Review> listReviews = reviewRepo.findByUserId(userId);
        boolean commented = listReviews.stream().filter(rev -> rev.getTourId().equals(tourId)).count() > 0;
        if (commented) throw new CustomRuntimeException("User has already commented on this tour");

        List<Booking> bookingList = bookingRepo.findByUserId(userId);
        if (bookingList == null || bookingList.isEmpty())
            throw new CustomRuntimeException("User haven't bought any tours");
        List<Booking> bList = bookingList.stream().filter(booking -> booking.getTourId().equals(tourId)).collect(Collectors.toList());
        boolean booked = bList != null && !bList.isEmpty();
        if (!booked) throw new CustomRuntimeException("User cannot comment on this tour");
        Booking b;
        if(bList.size()>1){
            b=bList.stream().filter(booking->booking.getId().equals(bookingId)).findFirst().orElse(null);
        }else{
            b=bList.get(0);
        }
        Map<String, Object> tourResult = helper.checkTourValid(tourId);
        Map<String, Object> userResult = helper.checkUserValid(userId);
        if (tourResult.get("valid").equals(false) || userResult.get("valid").equals(false) || rating < 0)
            throw new CustomRuntimeException("User or tour or rating points is invalid");
        Tour tour = (Tour) tourResult.get("data");
        User user = (User) userResult.get("data");
        Review r = new Review();
        r = new Review();
        r.setReview(review);
        r.setRating(rating);
        r.setTour(tour);
        r.setUser(user);
        r.setTourId(tourId);
        r.setUserId(userId);
        r.setBookingId(b.getId());
        r.setCreatedAt(LocalDateTime.now().toString());
        r.setShown(shown);
        r.setBookingId(bookingId);
        Review result = reviewRepo.save(r);
        List<Review> allReviewsOfTour = reviewRepo.findByTourId(tourId);
        double ratingsAverage = allReviewsOfTour.stream().mapToDouble(Review::getRating).average().orElse(0);
        tour.setRatingsAverage(ratingsAverage);
        tourRepo.save(tour);
        ReviewDto dto = new ReviewDto(result.getId(), result.getReview(), result.getRating(), result.getUser().getUsername(), result.getTour().getName(), result.getCreatedAt(), "", tourId, result.isShown(), result.getTour().getImageCover(), result.getUserId(), b.getStartDate(), result.getBookingId());
        return dto;
    }

    @Override
    public ReviewDto updateReview(String tourId, String userId, String review, double rating, boolean shown, String bookingId) throws CustomRuntimeException {
        List<Review> listReviews = reviewRepo.findByUserId(userId);
        boolean commented = listReviews.stream().filter(rev -> rev.getTourId().equals(tourId)).count() > 0;
        if (!commented) throw new CustomRuntimeException("No review found");
        Review r = listReviews.stream().filter(rev -> rev.getTourId().equals(tourId)).collect(Collectors.toList()).get(0);
        List<Booking> bookingList = bookingRepo.findByUserId(userId);
        if (bookingList == null || bookingList.isEmpty())
            throw new CustomRuntimeException("User haven't bought any tours");
        List<Booking> bList = bookingList.stream().filter(booking -> booking.getTourId().equals(tourId)).collect(Collectors.toList());
        boolean booked = bList != null && !bList.isEmpty();
        if (!booked) throw new CustomRuntimeException("User cannot comment on this tour");
        Booking b;
        if(bList.size()>1){
            b=bList.stream().filter(booking->booking.getId().equals(bookingId)).findFirst().orElse(null);
        }else{
            b=bList.get(0);
        }
        Map<String, Object> tourResult = helper.checkTourValid(tourId);
        Map<String, Object> userResult = helper.checkUserValid(userId);
        if (tourResult.get("valid").equals(false) || userResult.get("valid").equals(false) || rating < 0)
            throw new CustomRuntimeException("User or tour or rating points is invalid");
        Tour tour = (Tour) tourResult.get("data");
        User user = (User) userResult.get("data");
        if (rating < 0) throw new CustomRuntimeException("Rating points is invalid");
        r.setReview(review);
        r.setRating(rating);
        r.setReview(review);
        if (tour != null) r.setTour(tour);
        if (user != null) r.setUser(user);
        r.setLastUpdate(LocalDateTime.now().toString());
        r.setShown(shown);
        Review result = reviewRepo.save(r);
        List<Review> allReviewsOfTour = reviewRepo.findByTourId(tourId);
        double ratingsAverage = allReviewsOfTour.stream().mapToDouble(Review::getRating).average().orElse(0);
        if (tour != null) tour.setRatingsAverage(ratingsAverage);
        tourRepo.save(tour);
        ReviewDto dto = new ReviewDto(result.getId(), result.getReview(), result.getRating(), result.getUser().getUsername(), result.getTour().getName(), result.getCreatedAt(), result.getLastUpdate(), tourId, shown, result.getTour().getImageCover(), result.getUserId(), b.getStartDate(), result.getBookingId());

        return dto;
    }

    @Override
    public List<ReviewDto> findReviewsOfUser(String userId) throws CustomRuntimeException {
        List<Review> list = reviewRepo.findByUserId(userId);
        List<Booking> bookingList = bookingRepo.findByUserId(userId);
        if (bookingList == null || bookingList.isEmpty())
            throw new CustomRuntimeException("User haven't bought any tours");
        List<ReviewDto> dtos = list.stream().map(review -> new ReviewDto(review.getId(), review.getReview(), review.getRating(), review.getUser().getUsername(), review.getTour().getName(), review.getCreatedAt(), review.getLastUpdate(), review.getTourId(), review.isShown(), review.getTour().getImageCover(), review.getUserId(), getStartDate(review.getBookingId(), bookingList), review.getBookingId())).
        collect(Collectors.toList());
        return dtos;
    }

    private String getStartDate(String bookingId, List<Booking> list) {
        if (bookingId == null) return null;
        List<Booking> bList = list.stream().filter(booking -> booking.getId().equals(bookingId)).collect(Collectors.toList());
        if (bList == null || bList.isEmpty()) return null;
        Booking b = bList.get(0);
        return b.getStartDate();

    }

    @Override
    public List<ReviewDto> findReviewsOfTour(String tourId) throws CustomRuntimeException {
        List<Review> list = reviewRepo.findByTourId(tourId);
        List<Booking> bookingList = bookingRepo.findByTourId(tourId);
        if (bookingList == null || bookingList.isEmpty())
            return null;
        List<ReviewDto> dtos = list.stream().map(review -> new ReviewDto(review.getId(), review.getReview(), review.getRating(), review.getUser().getUsername(), review.getTour().getName(), review.getCreatedAt(), review.getLastUpdate(), review.getTourId(), review.isShown(), review.getTour().getImageCover(), review.getUserId(),  getStartDate(review.getBookingId(), bookingList), review.getBookingId())).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public List<ReviewDto> getReviewsOfUserAndTour(String tourId, String userId) throws CustomRuntimeException {
        List<Review> listReviews = reviewRepo.findByUserId(userId);
        boolean commented = listReviews.stream().filter(rev -> rev.getTourId().equals(tourId)).count() > 0;
        if (!commented) throw new CustomRuntimeException("No review found");
        List<Booking> bookingList = bookingRepo.findByTourId(tourId);
        if (bookingList == null || bookingList.isEmpty())
            throw new CustomRuntimeException("No booking of tour found!");
        List<Review> r = listReviews.stream().filter(rev -> rev.getTourId().equals(tourId)).collect(Collectors.toList());
        if(r==null||r.isEmpty()) return null;
        List<ReviewDto> dtos = r.stream().map(review -> new ReviewDto(review.getId(), review.getReview(), review.getRating(), review.getUser().getUsername(), review.getTour().getName(), review.getCreatedAt(), review.getLastUpdate(), review.getTourId(), review.isShown(), review.getTour().getImageCover(), review.getUserId(),  getStartDate(review.getBookingId(), bookingList), review.getBookingId())).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public void deleteReview(String reviewId) {
        Review r = reviewRepo.findById(reviewId).orElse(null);
        if (r != null) {
            String tourId = r.getTourId();
            reviewRepo.deleteById(reviewId);
            List<Review> allReviewsOfTour = reviewRepo.findByTourId(tourId);
            Tour tour = tourRepo.findById(tourId).orElse(null);
            if (tour != null) {
                double ratingsAverage = allReviewsOfTour.stream().mapToDouble(Review::getRating).average().orElse(0);
                tour.setRatingsAverage(ratingsAverage);
                tourRepo.save(tour);
            }

        }

    }

    @Override
    public void deleteAllReviewsOfTour(String tourId) throws CustomRuntimeException {
        List<Review> reviewList = reviewRepo.findByTourId(tourId);
        if (reviewList != null && reviewList.size() > 0) {
            try {
                Tour tour = tourRepo.findById(tourId).orElse(null);
                reviewRepo.deleteAllById(reviewList.stream().map(review -> review.getId()).distinct().collect(Collectors.toList()));
                if (tour != null) {
                    tour.setRatingsAverage(0);
                    tourRepo.save(tour);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new CustomRuntimeException("Error when trying to delete all reviews of tourId: " + tourId);
            }

        }
    }
}
