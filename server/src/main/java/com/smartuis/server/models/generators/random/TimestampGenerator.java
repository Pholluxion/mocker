package com.smartuis.server.models.generators.random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

public record TimestampGenerator(String name) implements IGenerator<String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @JsonCreator
    public TimestampGenerator(@JsonProperty("name") String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("timestamp generator: name cannot be null or empty");
        }
        this.name = name;
    }

    @Override
    public String sample() {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        return nowUtc.format(FORMATTER);
    }
}
