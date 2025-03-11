package com.smartuis.server.service.impl;

import com.smartuis.server.dtos.SimulationDTO;
import com.smartuis.server.repository.SchemaRepository;
import com.smartuis.server.repository.SimulationRepository;
import com.smartuis.server.service.SimulationService;
import com.smartuis.server.simulator.Simulator;
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
        return simulationRepository.exists(id)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return schemaRepository.findById(id)
                                .flatMap(simulationRepository::create)
                                .flatMap(simulationDTO -> simulationRepository.handleEvent(id, event));
                    }
                    return simulationRepository.handleEvent(id, event);
                });


    }

    @Override
    public Flux<String> logs(String id, int interval) {
        return simulationRepository.getById(id)
                .map(SimulationDTO::id)
                .flatMapMany(simulationId -> simulationRepository.logs(simulationId, interval));
    }

    @Override
    public Mono<Boolean> exists(String id) {
        return simulationRepository.exists(id);
    }


}
