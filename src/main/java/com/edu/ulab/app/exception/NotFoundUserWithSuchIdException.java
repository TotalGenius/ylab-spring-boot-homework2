package com.edu.ulab.app.exception;

public class NotFoundUserWithSuchIdException extends RuntimeException {
    public NotFoundUserWithSuchIdException(Long incorrectId){
        super("Not found user with such id:"+incorrectId);
    }
}
