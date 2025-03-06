package com.smartuis.server.models.generators.random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

import java.util.List;

public record StringGenerator(
        String name,
        List<String> values,
        String sampling,
        List<Double> weights
) implements IGenerator<String> {

    private static final List<String> VALID_STRATEGIES = List.of("random", "weighted");

    @JsonCreator
    public StringGenerator(
            @JsonProperty("name") String name,
            @JsonProperty("values") List<String> values,
            @JsonProperty("sampling") String sampling,
            @JsonProperty("weights") List<Double> weights
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("string generator: name must not be null or empty");
        }

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("string generator: values must not be null or empty");
        }

        if (sampling == null) {
            sampling = "random";
        }

        if (!VALID_STRATEGIES.contains(sampling)) {
            throw new IllegalArgumentException("string generator: sampling must be one of 'random' or 'weighted'");
        }

        if ("weighted".equals(sampling)) {
            if (weights == null || weights.size() != values.size()) {
                throw new IllegalArgumentException("string generator: weights must be provided and have the same size as values");
            }
            if (weights.stream().mapToDouble(Double::doubleValue).sum() <= 0) {
                throw new IllegalArgumentException("string generator: total weight must be positive");
            }
        } else if (weights != null) {
            throw new IllegalArgumentException("string generator: weights must not be provided when sampling is 'random'");
        }

        this.name = name;
        this.values = List.copyOf(values);
        this.sampling = sampling;
        this.weights = (weights != null) ? List.copyOf(weights) : null;
    }

    @Override
    public String sample() {
        return switch (sampling) {
            case "random" -> sampleRandom();
            case "weighted" -> sampleWeighted();
            default -> throw new IllegalStateException("Unknown sampling strategy: " + sampling);
        };
    }

    private String sampleRandom() {
        return values.get(Utils.RANDOM.nextInt(values.size()));
    }

    private String sampleWeighted() {
        double totalWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = Utils.RANDOM.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;

        for (int i = 0; i < values.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomValue < cumulativeWeight) {
                return values.get(i);
            }
        }
        return values.getLast();
    }
}