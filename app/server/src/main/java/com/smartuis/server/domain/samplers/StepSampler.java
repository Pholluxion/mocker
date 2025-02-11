package com.smartuis.server.domain.samplers;


import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.server.domain.interfaces.ISampler;

import com.smartuis.server.utils.constraints.ValidDurationInterval;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Flux;

import java.time.Duration;


@Getter
@AllArgsConstructor
@JsonTypeName("step")
@ValidDurationInterval
public class StepSampler implements ISampler {

    @NotNull(message = "step: duration must be provided (or 0)")
    @Min(value = 0, message = "step: duration must be at least 0 for infinite")
    private final Integer duration;

    @NotNull(message = "step: interval must be provided")
    @Min(value = 100, message = "step: interval must be at least 100 milliseconds")
    private final Integer interval;


    @Override
    public Flux<Integer> sample() {
        long count = (duration == 0) ? Integer.MAX_VALUE : duration / interval;

        return Flux.interval(Duration.ofMillis(interval))
                .map(i -> (int) interval)
                .take(count);
    }

}
