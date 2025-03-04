package com.smartuis.server.models.generators.discrete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record GeometricDistribution(String name, double probability) implements IGenerator<Integer> {


    @JsonCreator
    public GeometricDistribution(@JsonProperty("name") String name, @JsonProperty("probability") double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("geometric distribution: 0 <= probability <= 1");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("geometric distribution: name must not be null or empty");
        }

        this.probability = probability;
        this.name = name;
    }

    @Override
    public Integer sample() {
        return (int) Math.ceil(Math.log(1 - Utils.RANDOM.nextDouble()) / Math.log(1 - probability));

    }
}
