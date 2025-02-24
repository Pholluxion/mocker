package com.smartuis.server.dtos;

import lombok.Builder;
/**
 * ErrorDTO is a data transfer object that represents an error.
 * It contains details about the error and an associated message.
 */
@Builder
public record ErrorDTO(String error, String message) {
}