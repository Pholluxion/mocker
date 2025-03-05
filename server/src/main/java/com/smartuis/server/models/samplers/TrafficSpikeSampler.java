package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record TrafficSpikeSampler(Integer normal, Integer spike, Integer duration) implements ISampler {

    @JsonCreator
    public TrafficSpikeSampler(
            @JsonProperty("normal") Integer normal,
            @JsonProperty("spike") Integer spike,
            @JsonProperty("duration") Integer duration
    ) {
        if (normal == null || normal < 100) {
            throw new IllegalArgumentException("traffic spike sampler: normal must be greater than 100");
        }
        if (spike == null || spike < 10) {
            throw new IllegalArgumentException("traffic spike sampler: spike must be greater than 10");
        }
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("traffic spike sampler: duration must be positive");
        }
        this.normal = normal;
        this.spike = spike;
        this.duration = duration;
    }

    @Override
    public Flux<Long> sample() {
        Flux<Long> spikeFlux = Flux.interval(Duration.ofMillis(spike))
                .take(Duration.ofMillis(duration));
        Flux<Long> normalFlux = Flux.interval(Duration.ofMillis(normal));
        return Flux.concat(spikeFlux, normalFlux).repeat();
    }
}
