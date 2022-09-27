package com.edu.ulab.app.exception;

import com.edu.ulab.app.entity.Book;

public class IncorrectUserIdException extends RuntimeException {
    public IncorrectUserIdException(Long incorrectId) {
        super("Incorrect user id:"+incorrectId);
    }
}
