package com.smartuis.server.domain.interfaces;

import java.util.Random;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.smartuis.server.domain.generators.BooleanGenerator;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanGenerator.class, name = "boolean"),
})
public interface IGenerator<T> {


    T value();

    String name();

    static final Random random = new Random();

}
