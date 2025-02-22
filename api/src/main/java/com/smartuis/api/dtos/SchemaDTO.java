package com.smartuis.api.dtos;

import com.smartuis.api.models.schema.Schema;
import lombok.Builder;

@Builder
public record SchemaDTO(String id, String name) {
    public static SchemaDTO from(Schema schema) {
        return new SchemaDTO(schema.getId(), schema.getName());
    }
}