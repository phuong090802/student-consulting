package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.RoleException;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RoleControllerAdvice {
    @ExceptionHandler(value = RoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse handleRoleException(RoleException exception) {
        return new MessageResponse(false, exception.getMessage());
    }
}
