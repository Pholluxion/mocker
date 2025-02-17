package com.smartuis.api.models.generators;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.api.models.constraints.generators.BooleanValidator;
import com.smartuis.api.models.interfaces.IGenerator;

@JsonTypeName("boolean")
@BooleanValidator
public record BooleanGenerator(String name, Double probability) implements IGenerator<Boolean> {
    @Override
    public Boolean generate() {
        return Math.random() < probability;
    }
}
