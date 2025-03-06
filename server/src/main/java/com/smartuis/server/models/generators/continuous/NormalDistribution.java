package com.smartuis.server.models.generators.continuous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record NormalDistribution(String name, double mean, double stddev, int decimals) implements IGenerator<Double> {
    @JsonCreator
    public NormalDistribution(
            @JsonProperty("name") String name,
            @JsonProperty("mean") double mean,
            @JsonProperty("stddev") double stddev,
            @JsonProperty("decimals") int decimals
    ) {
        if (stddev <= 0)
            throw new IllegalArgumentException("normal distribution: standard deviation must be greater than 0");

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("normal distribution: name must not be null or empty");
        }

        if (decimals < 0) {
            throw new IllegalArgumentException("normal distribution: decimals cannot be negative");
        }

        if (decimals > 15) {
            throw new IllegalArgumentException("normal distribution: decimals cannot be greater than 15");
        }

        this.mean = mean;
        this.stddev = stddev;
        this.name = name;
        this.decimals = decimals;
    }

    @Override
    public Double sample() {
        return Utils.round(mean + stddev * Utils.RANDOM.nextGaussian(),decimals);
    }
}
