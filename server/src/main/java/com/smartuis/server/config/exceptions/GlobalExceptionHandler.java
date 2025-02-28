package com.smartuis.server.config.exceptions;


import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.samskivert.mustache.MustacheException;
import com.smartuis.server.dtos.ErrorDTO;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        return ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(message -> new ErrorDTO("Validation Error", message))
                .toList();
    }

    @ExceptionHandler(InvalidTypeIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleInvalidTypeIdException(InvalidTypeIdException ex, WebRequest request) {
        return new ErrorDTO("Invalid Type ID", "Invalid type id: " + ex.getTypeId());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return new ErrorDTO("Illegal Argument", ex.getMessage());
    }


    @ExceptionHandler(MustacheException.Context.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleMustacheException(MustacheException ex, WebRequest request) {
        return new ErrorDTO("Mustache Rendering Error", ex.getMessage());
    }


}