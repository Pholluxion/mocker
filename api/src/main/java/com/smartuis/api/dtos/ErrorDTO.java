package com.smartuis.api.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    private String error;
    private String message;
}