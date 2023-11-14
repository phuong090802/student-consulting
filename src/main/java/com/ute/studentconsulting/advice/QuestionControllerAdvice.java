package com.ute.studentconsulting.advice;

import com.ute.studentconsulting.exception.QuestionException;
import com.ute.studentconsulting.payloads.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class QuestionControllerAdvice {
    @ExceptionHandler(value = QuestionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse handleQuestionException(QuestionException exception) {
        return new MessageResponse(false, exception.getMessage());
    }
}
