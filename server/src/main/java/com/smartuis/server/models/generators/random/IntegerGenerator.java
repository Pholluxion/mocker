package com.smartuis.server.models.generators.random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record IntegerGenerator(String name, int min, int max) implements IGenerator<Integer> {
    @JsonCreator
    public IntegerGenerator(
            @JsonProperty("name") String name,
            @JsonProperty("min") int min,
            @JsonProperty("max") int max
    ) {
        if (min >= max) {
            throw new IllegalArgumentException("random integer distribution: min must be less than max");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("random integer distribution: name must not be null or empty");
        }

        this.min = min;
        this.max = max;
        this.name = name;
    }



    @Override
    public Integer sample() {
        return Utils.RANDOM.nextInt(max - min + 1) + min;
    }
}

