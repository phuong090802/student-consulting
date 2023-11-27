package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.ServerException;
import com.ute.studentconsulting.payloads.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServerControllerAdvice {
    @ExceptionHandler(value = ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerException(ServerException exception) {
        return new ErrorResponse(
                false,
                exception.getMessage(),
                exception.getDetail(),
                exception.getCode());
    }
}