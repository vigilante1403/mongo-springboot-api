package com.aptech.SemesterProject.exception;

public class DuplicateElementException extends ElementsManagementException{
    public DuplicateElementException(String message){
        super(message);

    }
    public DuplicateElementException(String message,Throwable cause){
        super(message, cause);
    }
}
