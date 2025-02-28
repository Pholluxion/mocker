package com.smartuis.server.models.generators.continuous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record UniformContinuousDistribution(String name, double min, double max,
                                            int decimals) implements IGenerator<Double> {

    @JsonCreator
    public UniformContinuousDistribution(
            @JsonProperty("name") String name,
            @JsonProperty("min") double min,
            @JsonProperty("max") double max,
            @JsonProperty("decimals") int decimals
    ) {
        if (min >= max) {
            throw new IllegalArgumentException("uniform continuous distribution: min must be less than max");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("uniform continuous distribution: name must not be null or empty");
        }

        if (decimals < 0) {
            throw new IllegalArgumentException("uniform continuous distribution: decimals cannot be negative");
        }

        if (decimals > 15) {
            throw new IllegalArgumentException("uniform continuous distribution: decimals cannot be greater than 15");
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