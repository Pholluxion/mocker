package com.smartuis.api.models.samplers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.api.models.constraints.samplers.SequentialValidator;
import com.smartuis.api.models.interfaces.ISampler;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

import java.util.List;

@JsonTypeName("sequential")
@SequentialValidator
public record SequentialSampler(@Valid List<StepSampler> steps) implements ISampler {
    @Override
    public Flux<Long> sample() {
        return Flux.fromIterable(steps)
                   .concatMap(StepSampler::sample);
    }
}
