package com.jobboard.jobboardapplication.ExceptionHandler;

import com.jobboard.jobboardapplication.auth.model.User;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
