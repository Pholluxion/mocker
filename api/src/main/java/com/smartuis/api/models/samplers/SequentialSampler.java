package com.smartuis.api.models.samplers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smartuis.api.models.interfaces.ISampler;

import reactor.core.publisher.Flux;

import java.util.List;

public record SequentialSampler(List<StepSampler> steps) implements ISampler {


    @JsonCreator
    public SequentialSampler(
            @JsonProperty("steps") List<StepSampler> steps
    ) {
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("sequential sampler requires steps");
        }
        this.steps = steps;
    }


    @Override
    public Flux<Long> sample() {
        return Flux.fromIterable(steps)
                   .concatMap(StepSampler::sample);
    }
}
