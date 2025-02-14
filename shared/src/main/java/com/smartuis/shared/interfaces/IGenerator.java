package com.smartuis.shared.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.smartuis.shared.generators.BooleanGenerator;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = BooleanGenerator.class, name = "boolean"),})
public interface IGenerator<T> {
    String name();
    T generate();
}
