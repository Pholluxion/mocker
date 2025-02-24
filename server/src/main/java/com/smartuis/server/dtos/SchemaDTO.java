package com.smartuis.server.dtos;

import com.smartuis.server.models.schema.Schema;
import lombok.Builder;

@Builder
public record SchemaDTO(String id, String name) {
    public static SchemaDTO from(Schema schema) {
        return new SchemaDTO(schema.getId(), schema.getName());
    }
}