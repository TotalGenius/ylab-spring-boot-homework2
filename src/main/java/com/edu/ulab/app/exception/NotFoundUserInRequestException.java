package com.edu.ulab.app.exception;

public class NotFoundUserInRequestException extends RuntimeException {
    public NotFoundUserInRequestException(){
        super("Person data not found in request");
    }
}
