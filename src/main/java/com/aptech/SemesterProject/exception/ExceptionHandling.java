package com.aptech.SemesterProject.exception;

import com.aptech.SemesterProject.dto.HttpResponse;
import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@CrossOrigin(origins = "http://localhost:3000",maxAge = 7200)
public class ExceptionHandling {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException ex){
        return  createHttpResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> noResultException(NoResultException ex){
        return  createHttpResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<HttpResponse> noSuchElementException(NoSuchElementException ex){
        return  createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
   @ExceptionHandler(DuplicateElementException.class)
   public ResponseEntity<HttpResponse> duplicateElementException(DuplicateElementException ex){
        return createHttpResponse(HttpStatus.CONFLICT,ex.getMessage());
   }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> duplicateElementException(AccessDeniedException ex){
        return createHttpResponse(HttpStatus.FORBIDDEN,"You don't have permission");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HttpResponse> handlingIllegalArgumentException(IllegalArgumentException ex){
        return createHttpResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }
    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<HttpResponse> customRuntimeException(Exception e) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message){
        HttpResponse res=new HttpResponse(httpStatus.value(),httpStatus, httpStatus.getReasonPhrase(),message);
        return new ResponseEntity<>(res,httpStatus);
    }
}
