package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.DepartmentException;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class DepartmentControllerAdvice {
    @ExceptionHandler(value = DepartmentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageResponse handleDepartmentException(DepartmentException exception) {
        return new MessageResponse(false, exception.getMessage());
    }
}
