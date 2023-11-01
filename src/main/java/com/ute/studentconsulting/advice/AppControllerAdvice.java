package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.AppException;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppControllerAdvice {
    @ExceptionHandler(value = AppException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageResponse handleRoleException(AppException exception) {
        return new MessageResponse(false, exception.getMessage());
    }
}