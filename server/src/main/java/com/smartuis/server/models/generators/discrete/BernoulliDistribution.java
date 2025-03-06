package com.smartuis.server.models.generators.discrete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;

public record BernoulliDistribution(String name, double probability) implements IGenerator<Integer> {


    @JsonCreator
    public BernoulliDistribution(@JsonProperty("name") String name, @JsonProperty("probability") double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("bernoulli distribution: 0 <= probability <= 1");
        }


        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("bernoulli distribution: name must not be null or empty");
        }

        this.probability = probability;
        this.name = name;
    }

    @Override
    public Integer sample() {
        return Math.random() < probability ? 1 : 0;
    }
}
