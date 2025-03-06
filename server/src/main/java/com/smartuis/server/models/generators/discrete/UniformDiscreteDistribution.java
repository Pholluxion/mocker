package com.smartuis.server.models.generators.discrete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record UniformDiscreteDistribution(String name, int min, int max) implements IGenerator<Integer> {

    @JsonCreator
    public UniformDiscreteDistribution(@JsonProperty("name") String name, @JsonProperty("min") int min, @JsonProperty("max") int max) {
        if (min > max) {
            throw new IllegalArgumentException("uniform discrete distribution: min <= max");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("uniform discrete distribution: name must not be null or empty");
        }

        this.min = min;
        this.max = max;
        this.name = name;
    }

    @Override
    public Integer sample() {
        return min + Utils.RANDOM.nextInt(max - min + 1);
    }
}
