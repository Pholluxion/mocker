package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record WindowSampler(Integer active, Integer inactive, Integer interval) implements ISampler {

    @JsonCreator
    public WindowSampler(
            @JsonProperty("active") Integer active,
            @JsonProperty("inactive") Integer inactive,
            @JsonProperty("interval") Integer interval
    ) {
        if (active == null || active <= 0) {
            throw new IllegalArgumentException("window sampler: active must be greater than 0");
        }
        if (inactive == null || inactive < 0) {
            throw new IllegalArgumentException("window sampler: inactive must be non-negative");
        }
        if (interval == null || interval < 100) {
            throw new IllegalArgumentException("window sampler: interval must be greater than 100");
        }
        this.active = active;
        this.inactive = inactive;
        this.interval = interval;
    }


    @Override
    public Flux<Long> sample() {
        Flux<Long> activePeriod = Flux.interval(Duration.ofMillis(interval))
                .take(Duration.ofMillis(active));
        Flux<Long> inactivePeriod = Flux.<Long>never()
                .take(Duration.ofMillis(inactive));
        return Flux.concat(activePeriod, inactivePeriod).repeat();
    }
}
