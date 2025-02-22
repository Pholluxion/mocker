package com.smartuis.api.config.exceptions;


import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.samskivert.mustache.MustacheException;
import com.smartuis.api.dtos.ErrorDTO;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        return ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(message -> ErrorDTO.builder()
                        .error("Validation Error")
                        .message(message)
                        .build())
                .toList();
    }

    @ExceptionHandler(InvalidTypeIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleInvalidTypeIdException(InvalidTypeIdException ex, WebRequest request) {
        return ErrorDTO.builder()
                .error("Invalid Type ID")
                .message("Invalid type id: " + ex.getTypeId())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return ErrorDTO.builder()
                .error("Illegal Argument")
                .message(ex.getMessage())
                .build();
    }


    @ExceptionHandler(MustacheException.Context.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleMustacheException(MustacheException ex, WebRequest request) {
        return ErrorDTO.builder()
                .error("Mustache Rendering Error")
                .message(ex.getMessage())
                .build();
    }


}