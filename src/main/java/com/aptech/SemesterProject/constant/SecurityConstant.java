package com.aptech.SemesterProject.constant;

public class SecurityConstant {
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final long EXPIRATION_TIME = 432000000;
    public static final String COMPANY = "My Company";
    public static final String APPLICATION_NAME = "Natours";
    public static final String[] API_PUBLIC_URLS ={
            "/api/v1/login","/api/v1/users/register",
            "/api/v1/unlockMe/**",
            "/api/v1/users/reset-password/**", "/api/v1/users/image/**",
    };
    public static final String[] API_PUBLIC_GET_URLS ={
            "/api/v1/products/**", "/api/v1/file/image/**", "/api/v1/tours/**"
    };
    public static final String[] RESOURCE_URLS = {
            "/css/**", "/images/**"
    };
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

}