package com.smartuis.server.models.generators.continuous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

public record TriangularDistribution(
        String name,
        double min,
        double max,
        double mode,
        int decimals
) implements IGenerator<Double> {


    @JsonCreator
    public TriangularDistribution(
            @JsonProperty("name") String name,
            @JsonProperty("min") double min,
            @JsonProperty("max") double max,
            @JsonProperty("mode") double mode,
            @JsonProperty("decimals") int decimals
    ) {
        if (min > mode || mode > max) throw new IllegalArgumentException("triangular distribution: min <= mode <= max");

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("triangular distribution: name must not be null or empty");
        }

        if (decimals < 0) {
            throw new IllegalArgumentException("triangular distribution: decimals cannot be negative");
        }

        if (decimals > 15) {
            throw new IllegalArgumentException("triangular distribution: decimals cannot be greater than 15");
        }


        this.min = min;
        this.max = max;
        this.mode = mode;
        this.name = name;
        this.decimals = decimals;
    }

    @Override
    public Double sample() {
        double f = (mode - min) / (max - min);
        double rand = Utils.RANDOM.nextDouble();
        if (rand < f) {
            return Utils.round(min + Math.sqrt(rand * (max - min) * (mode - min)), decimals);
        } else {
            return Utils.round(max - Math.sqrt((1 - rand) * (max - min) * (max - mode)), decimals);
        }
    }
}
