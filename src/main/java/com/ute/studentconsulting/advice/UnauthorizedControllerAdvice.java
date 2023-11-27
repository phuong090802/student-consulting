package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.UnauthorizedException;
import com.ute.studentconsulting.payloads.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UnauthorizedControllerAdvice {
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(UnauthorizedException exception) {
        return new ErrorResponse(
                false,
                exception.getMessage(),
                exception.getDetail(),
                exception.getCode());
    }
}
