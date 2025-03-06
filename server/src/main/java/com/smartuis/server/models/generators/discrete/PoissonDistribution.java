package com.smartuis.server.models.generators.discrete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;

public record PoissonDistribution(String name, double lambda) implements IGenerator<Integer> {


    @JsonCreator
    public PoissonDistribution(@JsonProperty("name") String name, @JsonProperty("lambda") double lambda) {
        if (lambda <= 0) {
            throw new IllegalArgumentException("poisson distribution: lambda must be positive");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("poisson distribution: name must not be null or empty");
        }

        this.lambda = lambda;
        this.name = name;
    }

    @Override
    public Integer sample() {
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > Math.exp(-lambda));

        return k - 1;
    }
}
