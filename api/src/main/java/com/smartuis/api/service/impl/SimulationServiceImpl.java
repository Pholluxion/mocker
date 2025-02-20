package com.smartuis.api.service.impl;

import com.smartuis.api.dtos.SimulationDTO;
import com.smartuis.api.repository.SchemaRepository;
import com.smartuis.api.repository.SimulationRepository;
import com.smartuis.api.service.SimulationService;
import com.smartuis.api.simulator.Simulator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SimulationServiceImpl implements SimulationService {


    private final SchemaRepository schemaRepository;
    private final SimulationRepository simulationRepository;

    public SimulationServiceImpl(SchemaRepository schemaRepository, SimulationRepository simulationRepository) {
       this.simulationRepository = simulationRepository;
        this.schemaRepository = schemaRepository;
    }

    @Override
    public Mono<SimulationDTO> create(String schemaId) {
        return schemaRepository
                .findById(schemaId)
                .flatMap(simulationRepository::create);
    }

    @Override
    public Mono<SimulationDTO> getById(String id) {
        return simulationRepository.getById(id);
    }

    @Override
    public Flux<SimulationDTO> findAll() {
        return simulationRepository.findAll();
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return simulationRepository.delete(id);
    }

    @Override
    public Mono<SimulationDTO> handleEvent(String id, Simulator.Event event) {
        return simulationRepository.handleEvent(id, event);
    }

    @Override
    public Flux<String> logs(String id) {
        return simulationRepository.logs(id);
    }


}
