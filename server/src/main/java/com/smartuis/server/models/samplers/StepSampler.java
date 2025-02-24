package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smartuis.server.models.interfaces.ISampler;

import reactor.core.publisher.Flux;

import java.time.Duration;

public record StepSampler(Integer duration, Integer interval) implements ISampler {

    @JsonCreator
    public StepSampler(
            @JsonProperty("duration") Integer duration,
            @JsonProperty("interval") Integer interval
    ) {

        if (duration == null || duration < 0) {
            throw new IllegalArgumentException("step sampler requires duration");
        }

        if (interval == null || interval < 1) {
            throw new IllegalArgumentException("step sampler requires interval");
        }

        if (interval < 100) {
            throw new IllegalArgumentException("step sampler requires interval to be greater than or equal to 100");
        }

        if (duration < interval && duration != 0) {
            throw new IllegalArgumentException("step sampler requires duration to be greater than or equal to interval");
        }

        this.duration = duration;
        this.interval = interval;
    }


    @Override
    public Flux<Long> sample() {
        long count = (duration == 0) ? Long.MAX_VALUE : duration / interval;

        return Flux.interval(Duration.ofMillis(interval)).take(count);
    }
}
