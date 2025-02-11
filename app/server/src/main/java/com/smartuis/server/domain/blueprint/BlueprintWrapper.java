package com.smartuis.server.domain.blueprint;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class BlueprintWrapper {

    @NotNull(message = "The simulation key is required")
    @Valid
    private final Blueprint simulation;

}
