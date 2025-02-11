package com.smartuis.server.domain.interfaces;

import java.util.Random;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.smartuis.server.domain.samplers.SequentialSampler;
import com.smartuis.server.domain.samplers.StepSampler;
import reactor.core.publisher.Flux;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SequentialSampler.class, name = "sequential"),
        @JsonSubTypes.Type(value = StepSampler.class,name = "step")
})
public interface ISampler {
    static final Random random = new Random();

    public abstract Flux<Integer> sample();
}
