package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record PulseSampler(Integer pulse, Integer idle) implements ISampler {

    @JsonCreator
    public PulseSampler(@JsonProperty("pulse") Integer pulse, @JsonProperty("idle") Integer idle) {

        if (pulse == null || pulse < 100) {
            throw new IllegalArgumentException("pulse sampler: pulse must be greater than 100");
        }
        if (idle == null || idle < 100) {
            throw new IllegalArgumentException("pulse sampler: idle must be greater than 100");
        }
        this.pulse = pulse;
        this.idle = idle;
    }

    @Override
    public Flux<Long> sample() {
        return Flux.concat(
                Flux.just(1L).delayElements(Duration.ofMillis(pulse)),
                Flux.just(0L).delayElements(Duration.ofMillis(idle))
        ).repeat();
    }
}