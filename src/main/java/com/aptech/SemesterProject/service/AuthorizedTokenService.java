package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.entity.AuthorizedToken;

public interface AuthorizedTokenService {
    //tao requested authorized token tu token
    AuthorizedToken makeNewAuthorizedToken(String token);
    //validate requested authorized token based on expiresTime or touched
    boolean validateAuthorizedToken(String authorized);

}
