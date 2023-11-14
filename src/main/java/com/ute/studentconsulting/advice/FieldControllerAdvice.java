package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.FieldException;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class FieldControllerAdvice {
    @ExceptionHandler(value = FieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse handleFieldException(FieldException exception) {
        return new MessageResponse(false, exception.getMessage());
    }
}
