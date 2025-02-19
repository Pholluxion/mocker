package com.smartuis.api.models.generators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.api.models.interfaces.IGenerator;

public record BooleanGenerator(String name, Double probability) implements IGenerator<Boolean> {

    @JsonCreator
    public  BooleanGenerator (
            @JsonProperty("name") String name,
            @JsonProperty("probability") Double probability
    ) {


        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("boolean generator name cannot be null or empty");
        }

        if (probability == null || probability < 0 || probability > 1) {
            throw new IllegalArgumentException("boolean generator probability must be between 0 and 1");
        }

        this.name = name;
        this.probability = probability;

    }


    @Override
    public Boolean generate() {
        return Math.random() < probability;
    }
}
