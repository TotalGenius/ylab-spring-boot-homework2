package com.edu.ulab.app.exception;

public class NotFoundBookWithSuchIdException extends RuntimeException {
    public NotFoundBookWithSuchIdException(Long incorrectId){
        super("Not found book with such id:"+incorrectId);
    }
}
