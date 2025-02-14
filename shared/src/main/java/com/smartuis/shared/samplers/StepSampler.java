package com.smartuis.shared.samplers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.shared.constraints.samplers.StepValidator;
import com.smartuis.shared.interfaces.ISampler;
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
