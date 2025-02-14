package com.smartuis.shared.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.smartuis.shared.interfaces.IGenerator;
import com.smartuis.shared.interfaces.ISampler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;


public record Schema(
        @Id String id,
        @Valid Schema.SchemaBuilder schema
) {

    @Data
    public static class SchemaBuilder {

        @NotBlank String name;
        @Valid ISampler sampler;
        @Valid List<IGenerator<?>> generators;
        @JsonSetter(value = "",nulls = Nulls.AS_EMPTY)
        String template;

        public Map<String, Object> generate() {
            return this
                    .generators
                    .stream()
                    .collect(Collectors.toMap(IGenerator::name, IGenerator::generate));
        }

    }

}
