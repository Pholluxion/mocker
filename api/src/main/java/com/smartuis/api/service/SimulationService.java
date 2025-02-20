package com.smartuis.api.service;

import com.smartuis.api.dtos.SimulationDTO;
import com.smartuis.api.simulator.Simulator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SimulationService {

    Mono<SimulationDTO> create(String schemaId);
    Mono<SimulationDTO> getById(String id);
    Flux<SimulationDTO> findAll();
    Mono<Boolean> delete(String id);
    Mono<SimulationDTO> handleEvent(String id, Simulator.Event event);
    Flux<String> logs(String id);


}
