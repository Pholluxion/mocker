package com.smartuis.server.models.generators.random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.IGenerator;
import com.smartuis.server.utils.Utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class DoubleListGenerator implements IGenerator<Double> {

  private final  String name;
    private final  List<Double> values;
    private final  String sampling;

    private static final List<String> VALID_STRATEGIES = List.of("sequential", "random") ;


    private  AtomicInteger currentIndex = new AtomicInteger(0);

    @JsonCreator
    public DoubleListGenerator(
            @JsonProperty("name") String name,
            @JsonProperty("values") List<Double> values,
            @JsonProperty("sampling") String sampling
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("double list generator: name must not be null or empty");
        }

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("double list generator: values must not be null or empty");
        }

        if (sampling == null) {
            sampling = "random";
        }

        if (!VALID_STRATEGIES.contains(sampling)) {
            throw new IllegalArgumentException("double list generator: sampling must be one of 'sequential', 'random' or 'weighted'");
        }

        this.name = name;
        this.values = List.copyOf(values);
        this.sampling = sampling;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Double sample() {
        return switch (sampling) {
            case "random" -> sampleRandom();
            case "sequential" -> sampleSequential();
            default -> throw new IllegalStateException("Unknown sampling strategy: " + sampling);
        };
    }

    private Double sampleRandom() {
        return values.get(Utils.RANDOM.nextInt(values.size()));
    }

    private Double sampleSequential() {
        if (currentIndex.get() >= values.size()) {
            currentIndex.set( 0);
        }
        return values.get( currentIndex.getAndIncrement());
    }

}