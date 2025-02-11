package com.smartuis.server.domain.samplers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.server.domain.interfaces.ISampler;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Flux;

@Getter
@AllArgsConstructor
@JsonTypeName("sequential")
public class SequentialSampler implements ISampler {

    @NotEmpty(message = "sequential: steps must be provided (at least one)")
    private final List<@Valid StepSampler> steps;

    @Override
    public Flux<Integer> sample() {
        return Flux.fromIterable(steps)
                .concatMap(StepSampler::sample);
    }
}
