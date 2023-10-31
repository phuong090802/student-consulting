package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.UserException;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UserControllerAdvice {
    @ExceptionHandler(value = UserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageResponse handleUserException(UserException exception) {
        return new MessageResponse(false, exception.getMessage());
    }
}
