package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartuis.server.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

public record TrafficSpikeSampler(Integer normalInterval,
                                  Integer spikeInterval,
                                  Integer spikeDuration,
                                  Integer normalDuration
) implements ISampler {

    @JsonCreator
    public TrafficSpikeSampler(
            @JsonProperty("normalInterval") Integer normalInterval,
            @JsonProperty("spikeInterval") Integer spikeInterval,
            @JsonProperty("spikeDuration") Integer spikeDuration,
            @JsonProperty("normalDuration") Integer normalDuration
    ) {

        if (normalInterval == null || normalInterval < 100) {
            throw new IllegalArgumentException("traffic spike sampler: normalInterval must be >= 100 ms");
        }
        if (spikeInterval == null || spikeInterval < 10) {
            throw new IllegalArgumentException("traffic spike sampler: spikeInterval must be >= 10 ms");
        }
        if (spikeDuration == null || spikeDuration <= 0) {
            throw new IllegalArgumentException("traffic spike sampler: spikeDuration must be positive");
        }
        if (normalDuration == null || normalDuration <= 0) {
            throw new IllegalArgumentException("traffic spike sampler: normalDuration must be positive");
        }

        this.normalDuration = normalDuration;
        this.spikeDuration = spikeDuration;
        this.spikeInterval = spikeInterval;
        this.normalInterval = normalInterval;

    }

    @Override
    public Flux<Long> sample() {
        long spikeEvents = spikeDuration / spikeInterval;

        Flux<Long> spikeFlux = Flux.interval(Duration.ofMillis(spikeInterval))
                .take(spikeEvents);

        long normalEvents = normalDuration / normalInterval;
        Flux<Long> normalFlux = Flux.interval(Duration.ofMillis(normalInterval))
                .take(normalEvents);

        return Flux.concat(spikeFlux, normalFlux).repeat();
    }
}
