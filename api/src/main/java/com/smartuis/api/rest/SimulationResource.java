package com.smartuis.api.rest;

import com.smartuis.api.simulator.Simulator;
import com.smartuis.api.dtos.SimulatorDTO;
import com.smartuis.api.service.SchemaService;
import com.smartuis.api.models.schema.Schema;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/v1/container")
public class SimulationResource {

    private final SchemaService schemaService;
    private final StateMachineFactory<Simulator.State, Simulator.Event> stateMachineFactory;
    private final Map<String, StateMachine<Simulator.State, Simulator.Event>> stateMachines;


    public SimulationResource(
            SchemaService schemaService,
            StateMachineFactory<Simulator.State, Simulator.Event> stateMachineFactory
    ) {
        this.schemaService = schemaService;
        this.stateMachineFactory = stateMachineFactory;
        this.stateMachines = new HashMap<>();

    }

    @PostConstruct
    private void createContainers() {
        var schemas = schemaService.findAll()
                .map(schema -> {
                    var stateMachine = stateMachineFactory.getStateMachine();
                    stateMachine.getExtendedState().getVariables().put("schema", schema);
                    stateMachines.put(schema.getId(), stateMachine);
                    return schema;
                }).collectList().block();

    }
    @GetMapping(value = "/state/{id}")
    public Mono<ResponseEntity<SimulatorDTO>> getCurrentState(@PathVariable String id) {

        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        var name = (Schema) stateMachine.getExtendedState().getVariables().get("schema");

        return Mono.just(ResponseEntity.ok(new SimulatorDTO(id,name.getName(), stateMachine.getState().getId().toString())));
    }



    @GetMapping("/event/{id}")
    public Mono<ResponseEntity<SimulatorDTO>> events(
            @PathVariable String id,
            @RequestParam Simulator.Event event
    ) {

        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        var message = MessageBuilder.withPayload(event).build();
        stateMachine.sendEvent(Mono.just(message)).subscribe();

        var currentState = stateMachine.getState().getId().toString();
        var name = (Schema) stateMachine.getExtendedState().getVariables().get("schema");


        return Mono.just(ResponseEntity.ok(new SimulatorDTO(id,name.getName(), currentState)));

    }


    @GetMapping("/create")
    public Mono<ResponseEntity<SimulatorDTO>> create(@RequestParam String schemaId) {

        var schema = schemaService.getById(schemaId).block();

        if (schema == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        var stateMachine = stateMachineFactory.getStateMachine();

        stateMachine.getExtendedState().getVariables().put("schema", schema);

        stateMachines.put(schemaId, stateMachine);



        var simulatorDTO = new SimulatorDTO(stateMachine.getId(),schema.getName(), Simulator.State.CREATED.toString());

        return Mono.just(ResponseEntity.ok(simulatorDTO));
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        stateMachine.stopReactively().subscribe();

        stateMachines.remove(id);

        return Mono.just(ResponseEntity.ok().build());
    }


    @GetMapping
    public Mono<ResponseEntity<List<SimulatorDTO>>> getAll() {
        final List<SimulatorDTO> containers = new ArrayList<>();
        stateMachines.forEach((key, value) -> {
            var id = value.getId();
            var state = value.getState();
            var name = (Schema) value.getExtendedState().getVariables().get("schema");
            containers.add(new SimulatorDTO(name.getId(),name.getName(), state.getId().toString()));
        });

        return Mono.just(ResponseEntity.ok(containers));
    }



    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<SimulatorDTO>> getContainers() {
        return Flux.interval(Duration.ofSeconds(5)).map(tick -> {
            final List<SimulatorDTO> containers = new ArrayList<>();
            stateMachines.forEach((key, value) -> {
                var id = value.getId();
                var state = value.getState();
                var name = (Schema) value.getExtendedState().getVariables().get("schema");
                containers.add(new SimulatorDTO(name.getId(),name.getName(), state.getId().toString()));
            });
            return containers;
        });
    }
}
