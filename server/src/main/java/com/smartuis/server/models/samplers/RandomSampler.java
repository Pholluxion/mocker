package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import com.smartuis.server.utils.Utils;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record RandomSampler(Integer min, Integer max) implements ISampler {

    @JsonCreator
    public RandomSampler(@JsonProperty("min") Integer min, @JsonProperty("max") Integer max) {

        if (min == null || max == null || min < 100 || max <= min) {
            throw new IllegalArgumentException("random interval sampler: invalid delay range");
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public Flux<Long> sample() {
        return Flux.interval(Duration.ofMillis(Utils.RANDOM.nextInt(min, max)));
    }
}
