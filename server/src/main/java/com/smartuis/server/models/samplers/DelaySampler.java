package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record DelaySampler(Integer delay) implements ISampler {

    @JsonCreator
    public DelaySampler(@JsonProperty("delay") Integer delay) {
        if (delay == null || delay < 100) {
            throw new IllegalArgumentException("delay sampler: delay must be greater than 100");
        }
        this.delay = delay;
    }


    @Override
    public Flux<Long> sample() {
        return Flux.interval(Duration.ofMillis(delay));
    }


}

