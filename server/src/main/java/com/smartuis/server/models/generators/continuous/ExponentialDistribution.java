package com.smartuis.server.models.generators.continuous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record ExponentialDistribution(String name, double lambda, int decimals) implements IGenerator<Double> {
    @JsonCreator
    public ExponentialDistribution(
            @JsonProperty("name") String name,
            @JsonProperty("lambda") double lambda,
            @JsonProperty("decimals") int decimals
    ) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("exponential distribution: name must not be null or empty");
        }

        if (lambda <= 0) {
            throw new IllegalArgumentException("exponential distribution: lambda must be greater than 0");
        }

        if (decimals < 0) {
            throw new IllegalArgumentException("exponential distribution: decimals cannot be negative");
        }

        if (decimals > 15) {
            throw new IllegalArgumentException("exponential distribution: decimals cannot be greater than 15");
        }

        this.lambda = lambda;
        this.name = name;
        this.decimals = decimals;
    }

    @Override
    public Double sample() {
        return Utils.round(-Math.log(1 - Utils.RANDOM.nextDouble()) / lambda, decimals);
    }


}
