package com.smartuis.server.models.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.smartuis.server.models.samplers.*;
import reactor.core.publisher.Flux;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SequentialSampler.class, name = "sequential"),
        @JsonSubTypes.Type(value = StepSampler.class, name = "step"),
        @JsonSubTypes.Type(value = BurstSampler.class, name = "burst"),
        @JsonSubTypes.Type(value = DelaySampler.class, name = "delay"),
        @JsonSubTypes.Type(value = CountSampler.class, name = "count"),
        @JsonSubTypes.Type(value = RandomSampler.class, name = "random"),
        @JsonSubTypes.Type(value = LoopSampler.class, name = "loop"),
        @JsonSubTypes.Type(value = PulseSampler.class, name = "pulse"),
        @JsonSubTypes.Type(value = TrafficSpikeSampler.class, name = "traffic-spike"),
        @JsonSubTypes.Type(value = WindowSampler.class, name = "window"),
})
public interface ISampler {

    Flux<Long> sample();
}
