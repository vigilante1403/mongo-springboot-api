package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.dto.*;
import com.aptech.SemesterProject.entity.Review;
import com.aptech.SemesterProject.entity.Tour;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.BookingService;
import com.aptech.SemesterProject.service.ReviewService;
import com.aptech.SemesterProject.service.UserService;
import com.aptech.SemesterProject.service.cache.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Transactional
@Qualifier("myUserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ReviewService reviewService;
    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    public User addUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User findUserWithEmailAndToken(String email, String token,String typeToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        query.addCriteria(Criteria.where(typeToken).is(token));
        List<User> list = mongoTemplate.find(query,User.class);
        System.out.println(list);
        if(list.isEmpty())return null;
        return list.get(0);
    }
    @Override
    public void deleteUser(String userId) {
        User user = userRepo.findById(userId).orElse(null);
        if(user!=null) userRepo.delete(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        User u=userRepo.findByEmail(email).get(0);
        if(u==null) return null;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(password,u.getPassword());
        if(matches) return u;
        return null;
    }

    @Override
    public List<GuideDto> getAllGuides() {
        Query q = new Query();
        q.addCriteria(Criteria.where("role").is("GUIDE"));
        List<User> list = mongoTemplate.find(q,User.class);
        List<GuideDto> list1 = list.stream().map(guide->new GuideDto(guide.getId(),guide.getEmail(),guide.getName())).collect(Collectors.toList());
        return list1;
    }

    @Override
    public UserWithFullReviewsAndBookingsDto getUser(String userId) throws CustomRuntimeException {
        User u =null;
        List<BookingDto> bookedList=null;
        List<TourDto> joinedTourDtos =null;
        List<ReviewDto> reviewsMade = null;
        UserWithFullReviewsAndBookingsDto dto=null;
        u = userRepo.findById(userId).orElse(null);
        if(u!=null){
             bookedList = bookingService.getBookingsByUser(userId);
            if(bookedList!=null&&bookedList.size()>0){
                List<Tour> joinedTours = bookedList.stream().map(booking->booking.getTour()).collect(Collectors.toList());
                if(joinedTours!=null&& !joinedTours.isEmpty()){
                    joinedTourDtos = joinedTours.stream().map(tour->new TourDto(tour.getId(),tour.getName(),tour.getPrice(),tour.getGuides().stream().map(guide->userRepo.findById(guide.toString()).get()).collect(Collectors.toSet()), tour.getImageCover(),tour.getDescription(),tour.getSummary(),tour.getMaxGroupSize(),tour.getPriceDiscount(),tour.getStartDates().size()>0?tour.getStartDates():new HashSet<LocalDate>(),tour.getCountryNameCommon(),tour.getCountryNameOfficial(),tour.getCountryFlag(),tour.getRegion(),tour.getStatus(),tour.getImages()!=null?tour.getImages():new HashSet<>())).collect(Collectors.toList());
                }
                reviewsMade = reviewService.findReviewsOfUser(userId);
            }
            dto = new UserWithFullReviewsAndBookingsDto(u.getId(), u.getName(), u.getEmail(), u.getPhoto(), u.getFullName(),u.getNationality(),u.getNationalID(), u.getCountryFlag(), u.getRole().toString(), u.getCreatedAt()+"", u.getLastLoginDate()!=null?u.getLastLoginDate().toString():"", u.isActive(), u.isNotLocked(), joinedTourDtos, reviewsMade, bookedList);
            System.out.println(dto.getCreatedAt());
        }
        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).get(0);

        if (user == null) {
            throw new RuntimeException("user not found by username: " + username);
        } else {

            if (user.isNotLocked()) {
                try {
                    if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
//                        user.setNotLocked(false);

                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
            }

            user.setLastLoginDate(user.getCurrentLoginDate());
            user.setCurrentLoginDate(new Date());
            User userPrincipal = userRepo.save(user);
            return userPrincipal;
        }
    }
}
