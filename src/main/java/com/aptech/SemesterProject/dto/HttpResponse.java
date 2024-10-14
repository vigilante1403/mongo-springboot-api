package com.aptech.SemesterProject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse {
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "MM-dd-yyyy HH:mm:ss",timezone = "Asia/Ho_Chi_Minh")
    private Date timestamp;
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String response;
    private String message;

    public HttpResponse( int httpStatusCode, HttpStatus httpStatus, String response, String message) {
        this.timestamp=new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.response = response;
        this.message = message;
    }
}
