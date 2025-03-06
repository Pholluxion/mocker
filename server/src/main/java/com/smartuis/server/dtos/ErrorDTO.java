package com.smartuis.server.dtos;

/**
 * ErrorDTO is a data transfer object that represents an error.
 * It contains details about the error and an associated message.
 */
public record ErrorDTO(String error, String message) {
}