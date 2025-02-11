package com.smartuis.server.domain.blueprint;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.smartuis.server.domain.interfaces.IGenerator;
import com.smartuis.server.domain.interfaces.ISampler;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Blueprint {

    @Id
    private String uuid;

    @NotNull(message = "The name is required")
    @NotBlank(message = "The name is required and must not be empty")
    @Size(min = 4, message = "The name must be at least 4 characters long")
    private final String name;

    @NotNull(message = "The sampler is required")
    @Valid
    private final ISampler sampler;

    @NotEmpty(message = "At least one generator must be provided")
    @Valid
    private final List<IGenerator<?>> generators;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private  String template;

}
