package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record LoopSampler(Integer delay) implements ISampler {

    @JsonCreator
    public LoopSampler(@JsonProperty("delay") Integer delay) {
        if (delay == null || delay < 10) {
            throw new IllegalArgumentException("infinite loop sampler: delay must be greater than 10");
        }
        this.delay = delay;
    }

    @Override
    public Flux<Long> sample() {
        return Flux.interval(Duration.ofMillis(delay)).take(Long.MAX_VALUE);
    }
}
