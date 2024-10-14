package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.entity.AuthorizedToken;
import com.aptech.SemesterProject.service.AuthorizedTokenService;
import com.aptech.SemesterProject.utility.JWTTokenProvider;
import com.aptech.SemesterProject.utility.SpringConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizedTokenServiceImpl implements AuthorizedTokenService {
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private SpringConfig springConfig;
    @Override
    public AuthorizedToken makeNewAuthorizedToken(String token) {
        //token dung de xac dinh admin hay leadguide add booking in past
        return null;
    }

    @Override
    public boolean validateAuthorizedToken(String authorized) {
        return false;
    }
}
