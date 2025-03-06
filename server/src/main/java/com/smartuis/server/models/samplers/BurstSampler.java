package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record BurstSampler(Integer size, Integer delay) implements ISampler {

    @JsonCreator
    public BurstSampler(@JsonProperty("size") Integer size, @JsonProperty("delay") Integer delay) {

        if (size == null || size <= 0) {
            throw new IllegalArgumentException("burst sampler: size must be greater than 0");
        }
        if (delay == null || delay < 100) {
            throw new IllegalArgumentException("burst sampler: delay must be greater than 100");
        }
        this.size = size;
        this.delay = delay;
    }

    @Override
    public Flux<Long> sample() {
        return Flux.interval(Duration.ofMillis(delay)).flatMap(i -> Flux.range(0, size).map(Long::valueOf));
    }
}
