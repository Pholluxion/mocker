package com.smartuis.shared.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.smartuis.shared.samplers.SequentialSampler;
import com.smartuis.shared.samplers.StepSampler;
import reactor.core.publisher.Flux;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
                @JsonSubTypes.Type(value = SequentialSampler.class, name = "sequential"),
                @JsonSubTypes.Type(value = StepSampler.class, name = "step")
})
public interface ISampler {

    Flux<Long> sample();
}
