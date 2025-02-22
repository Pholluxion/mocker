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

/**
 * SimulationResource is a REST controller that provides endpoints for managing simulations.
 */
@RestController
@RequestMapping("/api/v1/simulation")
public class SimulationResource {

    private final SimulationService simulationService;

    /**
     * Constructs a SimulationResource with the given SimulationService.
     *
     * @param simulationService the service to handle simulation operations
     */
    public SimulationResource(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    /**
     * Starts a simulation with the given ID.
     *
     * @param id the ID of the simulation to start
     * @return a Mono containing the ResponseEntity with the SimulationDTO or a not found status
     */
    @GetMapping("/start/{id}")
    public Mono<ResponseEntity<SimulationDTO>> start(@PathVariable String id) {
        return simulationService
                .handleEvent(id, Simulator.Event.START)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Stops a simulation with the given ID.
     *
     * @param id the ID of the simulation to stop
     * @return a Mono containing the ResponseEntity with the SimulationDTO or a not found status
     */
    @GetMapping("/stop/{id}")
    public Mono<ResponseEntity<SimulationDTO>> stop(@PathVariable String id) {
        return simulationService
                .handleEvent(id, Simulator.Event.STOP)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Kills a simulation with the given ID.
     *
     * @param id the ID of the simulation to kill
     * @return a Mono containing the ResponseEntity with the SimulationDTO or a not found status
     */
    @GetMapping("/kill/{id}")
    public Mono<ResponseEntity<SimulationDTO>> kill(@PathVariable String id) {
        return simulationService
                .handleEvent(id, Simulator.Event.KILL)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new simulation with the given ID.
     *
     * @param id the ID of the simulation to create
     * @return a Mono containing the ResponseEntity with the SimulationDTO or a not found status
     */
    @GetMapping("/create/{id}")
    public Mono<ResponseEntity<SimulationDTO>> create(@PathVariable String id) {
        return simulationService
                .create(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all simulations.
     *
     * @return a Mono containing the ResponseEntity with the list of SimulationDTOs
     */
    @GetMapping
    public Mono<ResponseEntity<List<SimulationDTO>>> getAll() {
        return simulationService
                .findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }

    /**
     * Retrieves the state of a simulation with the given ID.
     *
     * @param id the ID of the simulation to retrieve the state of
     * @return a Mono containing the ResponseEntity with the SimulationDTO or a not found status
     */
    @GetMapping("/state/{id}")
    public Mono<ResponseEntity<SimulationDTO>> state(@PathVariable String id) {
        return simulationService
                .getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Streams the list of all simulations every second.
     *
     * @return a Flux containing the list of SimulationDTOs
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<List<SimulationDTO>> stream() {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(i -> simulationService.findAll().collectList());
    }

    /**
     * Deletes a simulation with the given ID.
     *
     * @param id the ID of the simulation to delete
     * @return a Mono containing the ResponseEntity with a boolean indicating success or a not found status
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Boolean>> delete(@PathVariable String id) {
        return simulationService
                .delete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Streams the logs of a simulation with the given ID.
     *
     * @param id the ID of the simulation to stream logs for
     * @return a Flux containing the log messages
     */
    @GetMapping(value = "/logs/{id}", produces = "text/event-stream")
    public Flux<String> logs(@PathVariable String id) {
        return simulationService.logs(id);
    }
}