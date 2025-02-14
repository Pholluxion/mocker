package com.smartuis.api.config.exceptions;


import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.samskivert.mustache.MustacheException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final  String ERROR = "error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
    }

    @ExceptionHandler(InvalidTypeIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidTypeIdException(InvalidTypeIdException ex) {
        String typeId = ex.getTypeId();
        return Map.of(ERROR, "The type id '" + typeId + "' is invalid.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Map.of(ERROR, ex.getMessage());
    }

    @ExceptionHandler(MustacheException.Context.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMustacheException(MustacheException ex) {
        return Map.of(ERROR, ex.getMessage());
    }

}