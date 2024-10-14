package com.aptech.SemesterProject.exception;

import com.aptech.SemesterProject.dto.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error->{
                    String message = error.getDefaultMessage();
                    errors.append(message);
                    errors.append("; ");
                });
        HttpResponse httpResponse = new HttpResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors.toString()
                );
        return new ResponseEntity<Object>(httpResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> get500Error(Exception ex){
        log.error(ex.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message){
        HttpResponse res=new HttpResponse(httpStatus.value(),httpStatus, httpStatus.getReasonPhrase(),message);
        return new ResponseEntity<>(res,httpStatus);
    }
}
