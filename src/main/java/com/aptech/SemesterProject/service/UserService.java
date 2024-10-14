package com.aptech.SemesterProject.service;


import com.aptech.SemesterProject.dto.GuideDto;
import com.aptech.SemesterProject.dto.UserWithFullReviewsAndBookingsDto;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.exception.CustomRuntimeException;

import java.util.List;

public interface UserService  {
    List<User> getAllUser();
    User addUser(User user);
    User findUserWithEmailAndToken(String email,String token,String typeToken);
    void deleteUser(String userId);
    User updateUser(User user);
    User login(String email,String password);
    List<GuideDto> getAllGuides();
    UserWithFullReviewsAndBookingsDto getUser(String userId) throws CustomRuntimeException;



}
