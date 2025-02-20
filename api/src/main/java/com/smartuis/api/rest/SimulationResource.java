package com.smartuis.api.rest;

import com.smartuis.api.service.SimulationService;
import com.smartuis.api.simulator.Simulator;
import com.smartuis.api.dtos.SimulationDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/v1/simulation")
public class SimulationResource {

    private final SimulationService simulationService;

    public SimulationResource(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/start/{id}")
    public Mono<ResponseEntity<SimulationDTO>> startSimulation(@PathVariable String id) {
        return simulationService
                .handleEvent(id, Simulator.Event.START)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/stop/{id}")
    public Mono<ResponseEntity<SimulationDTO>> stopSimulation(@PathVariable String id) {
        return simulationService
                .handleEvent(id, Simulator.Event.STOP)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/kill/{id}")
    public Mono<ResponseEntity<SimulationDTO>> killSimulation(@PathVariable String id) {
        return simulationService
                .handleEvent(id, Simulator.Event.KILL)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @GetMapping("/create/{id}")
    public Mono<ResponseEntity<SimulationDTO>> createSimulation(@PathVariable String id) {
        return simulationService
                .create(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @GetMapping
    public Mono<ResponseEntity<List<SimulationDTO>>> getAllSimulations() {
        return simulationService
                .findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/state/{id}")
    public Mono<ResponseEntity<SimulationDTO>> getSimulationState(@PathVariable String id) {
        return simulationService
                .getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<List<SimulationDTO>> streamSimulations() {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(i -> simulationService.findAll().collectList());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Boolean>> deleteSimulation(@PathVariable String id) {
        return simulationService
                .delete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/logs/{id}", produces = "text/event-stream")
    public Flux<String> getLogs(@PathVariable String id) {
        return simulationService.logs(id);
    }


}
