package com.aptech.SemesterProject.exception;

public class ElementsManagementException extends RuntimeException{
    public ElementsManagementException(String message){
        super(message);

    }
    public ElementsManagementException(String message,Throwable cause){
        super(message,cause);
    }
}
