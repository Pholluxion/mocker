package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record CountSampler(Integer delay, Integer count) implements ISampler {

    @JsonCreator
    public CountSampler(@JsonProperty("delay") Integer delay, @JsonProperty("count") Integer count) {

        if (delay == null || delay < 100) {
            throw new IllegalArgumentException("count sampler: delay must be greater than 100");
        }
        if (count == null || count <= 0) {
            throw new IllegalArgumentException("count sampler: count must be greater than 0");
        }
        this.delay = delay;
        this.count = count;
    }

    @Override
    public Flux<Long> sample() {
        return Flux.interval(Duration.ofMillis(delay)).take(count);
    }
}