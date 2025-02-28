package com.smartuis.server.dtos;

/**
 * SchemaDTO is a data transfer object that represents a schema.
 * It contains an identifier, a name, and a status flag.
 *
 * @param id The unique identifier of the schema.
 * @param name The name of the schema.
 * @param status A flag indicating if the schema simulation is status.
 */
public record SchemaDTO(String id, String name, Boolean status) {}