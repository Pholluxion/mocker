package com.smartuis.server.models.generators.discrete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record BinomialDistribution(String name, int trials, double probability) implements IGenerator<Integer> {


    @JsonCreator
    public BinomialDistribution(@JsonProperty("name") String name, @JsonProperty("trials") int trials, @JsonProperty("p") double probability) {

        if (trials < 0) {
            throw new IllegalArgumentException("binomial distribution: trials must be non-negative");
        }

        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("binomial distribution: 0 <= probability <= 1");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("binomial distribution: name must not be null or empty");
        }

        this.trials = trials;
        this.probability = probability;
        this.name = name;
    }

    @Override
    public Integer sample() {
        int x = 0;
        for (int i = 0; i < trials; i++) {
            if (Utils.RANDOM.nextDouble() < probability) x++;
        }
        return x;
    }
}
