package com.smartuis.shared.generators;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.shared.constraints.generators.BooleanValidator;
import com.smartuis.shared.interfaces.IGenerator;

@JsonTypeName("boolean")
@BooleanValidator
public record BooleanGenerator(String name, Double probability) implements IGenerator<Boolean> {
    @Override
    public Boolean generate() {
        return Math.random() < probability;
    }
}
