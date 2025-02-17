package com.smartuis.api.models.samplers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.api.models.constraints.samplers.StepValidator;
import com.smartuis.api.models.interfaces.ISampler;
import reactor.core.publisher.Flux;

import java.time.Duration;

@JsonTypeName("step")
@StepValidator
public record StepSampler(Integer duration, Integer interval) implements ISampler {

    @Override
    public Flux<Long> sample() {
        long count = (duration == 0) ? Long.MAX_VALUE : duration / interval;

        return Flux.interval(Duration.ofMillis(interval))
                .take(count);
    }
}
