package com.smartuis.server.models.generators.random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;

import java.time.*;
import java.time.format.DateTimeFormatter;

public record TimestampGenerator(String name, String format) implements IGenerator<String> {

    @JsonCreator
    public TimestampGenerator(@JsonProperty("name") String name,
                              @JsonProperty("format") String format
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("timestamp generator: name cannot be null or empty");
        }

        String resolvedFormat = (format == null || format.isBlank()) ? "yyyy-MM-dd HH:mm:ss" : format;

        this.name = name;
        this.format = resolvedFormat;
    }

    @Override
    public String sample() {
        String format = this.format;
        DateTimeFormatter formatter = fromRaw(format);

        return switch (format) {
            case "ISO_DATE", "ISO_LOCAL_DATE", "ISO_OFFSET_DATE", "ISO_ORDINAL_DATE", "ISO_WEEK_DATE", "BASIC_ISO_DATE" ->
                    LocalDate.now().format(formatter);

            case "ISO_TIME", "ISO_LOCAL_TIME" ->
                    LocalTime.now().format(formatter);

            case "ISO_OFFSET_TIME" ->
                    OffsetTime.now(ZoneOffset.UTC).format(formatter);

            case "ISO_DATE_TIME", "ISO_LOCAL_DATE_TIME" ->
                    LocalDateTime.now().format(formatter);

            case "ISO_OFFSET_DATE_TIME" ->
                    OffsetDateTime.now(ZoneOffset.UTC).format(formatter);

            case "ISO_ZONED_DATE_TIME", "RFC_1123_DATE_TIME" ->
                    ZonedDateTime.now(ZoneId.of("UTC")).format(formatter);

            case "ISO_INSTANT" ->
                    Instant.now().toString();

            default ->
                    LocalDateTime.now().format(formatter);
        };
    }



    private DateTimeFormatter fromRaw(String format) {

        return switch (format) {
            case "ISO_DATE" -> DateTimeFormatter.ISO_DATE;
            case "ISO_DATE_TIME" -> DateTimeFormatter.ISO_DATE_TIME;
            case "ISO_LOCAL_DATE" -> DateTimeFormatter.ISO_LOCAL_DATE;
            case "ISO_LOCAL_DATE_TIME" -> DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            case "ISO_OFFSET_DATE" -> DateTimeFormatter.ISO_OFFSET_DATE;
            case "ISO_OFFSET_DATE_TIME" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            case "ISO_TIME" -> DateTimeFormatter.ISO_TIME;
            case "ISO_LOCAL_TIME" -> DateTimeFormatter.ISO_LOCAL_TIME;
            case "ISO_ZONED_DATE_TIME" -> DateTimeFormatter.ISO_ZONED_DATE_TIME;
            case "ISO_INSTANT" -> DateTimeFormatter.ISO_INSTANT;
            case "ISO_ORDINAL_DATE" -> DateTimeFormatter.ISO_ORDINAL_DATE;
            case "ISO_WEEK_DATE" -> DateTimeFormatter.ISO_WEEK_DATE;
            case "ISO_OFFSET_TIME" -> DateTimeFormatter.ISO_OFFSET_TIME;
            case "BASIC_ISO_DATE" -> DateTimeFormatter.BASIC_ISO_DATE;
            case "RFC_1123_DATE_TIME" -> DateTimeFormatter.RFC_1123_DATE_TIME;

            default -> DateTimeFormatter.ofPattern(format);
        };
    }


}
