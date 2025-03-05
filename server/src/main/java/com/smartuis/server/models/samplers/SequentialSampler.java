package com.smartuis.server.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smartuis.server.models.interfaces.ISampler;

import reactor.core.publisher.Flux;

import java.util.List;

public record SequentialSampler(List<ISampler> steps) implements ISampler {


    @JsonCreator
    public SequentialSampler(@JsonProperty("steps") List<ISampler> steps) {

        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("sequential sampler requires steps");
        }
        this.steps = steps;
    }


    @Override
    public Flux<Long> sample() {
        return Flux.fromIterable(steps).concatMap(ISampler::sample);
    }
}
