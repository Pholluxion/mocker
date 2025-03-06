package com.smartuis.server.models.generators.random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record DoubleGenerator(String name, int min, int max, int decimals) implements IGenerator<Double> {


    @JsonCreator
    public DoubleGenerator(
            @JsonProperty("name") String name,
            @JsonProperty("min") int min,
            @JsonProperty("max") int max,
            @JsonProperty("decimals") int decimals
    ) {
        if (min >= max) {
            throw new IllegalArgumentException("random double distribution: min must be less than max");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("random double distribution: name must not be null or empty");
        }

        if (decimals < 0) {
            throw new IllegalArgumentException("random double distribution: decimals cannot be negative");
        }

        if (decimals > 15) {
            throw new IllegalArgumentException("random double distribution: decimals cannot be greater than 15");
        }

        this.min = min;
        this.max = max;
        this.name = name;
        this.decimals = decimals;
    }


    @Override
    public Double sample() {
        return Utils.round(min + (max - min) * Utils.RANDOM.nextDouble(), decimals);
    }
}