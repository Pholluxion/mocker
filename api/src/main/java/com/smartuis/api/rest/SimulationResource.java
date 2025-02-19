package com.smartuis.api.rest;

import com.smartuis.api.config.amqp.AmqpConnector;
import com.smartuis.api.config.mqtt.MqttConnector;
import com.smartuis.api.simulator.Simulator;
import com.smartuis.api.dtos.SimulatorDTO;
import com.smartuis.api.service.SchemaService;
import com.smartuis.api.models.schema.Schema;

import jakarta.annotation.PostConstruct;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/v1/container")
public class SimulationResource {

    private final Simulator simulator;
    private final SchemaService schemaService;
    private final Map<String, StateMachine<Simulator.State, Simulator.Event>> stateMachines;


    public SimulationResource(Simulator simulator, SchemaService schemaService) {
        this.schemaService = schemaService;
        this.stateMachines = new HashMap<>();
        this.simulator = simulator;

    }

    @PostConstruct
    private void createContainers() {
        schemaService.findAll().map(schema -> {

            try {
                StateMachine<Simulator.State, Simulator.Event> stateMachine = simulator.createStateMachine();

                stateMachine.getExtendedState().getVariables().put("schema", schema);
                stateMachine.getExtendedState().getVariables().put("mqtt", new MqttConnector());
                stateMachine.getExtendedState().getVariables().put("amqp", new AmqpConnector());
                stateMachine.getExtendedState().getVariables().put("isRunning", false);

                stateMachines.put(schema.getId(), stateMachine);
                return schema;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }).collectList().block();

    }

    @GetMapping(value = "/state/{id}")
    public Mono<ResponseEntity<SimulatorDTO>> getCurrentState(@PathVariable String id) {

        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        var name = (Schema) stateMachine.getExtendedState().getVariables().get("schema");

        return Mono.just(ResponseEntity.ok(new SimulatorDTO(id, name.getName(), stateMachine.getState().getId().toString())));
    }


    @GetMapping("/event/{id}")
    public Mono<ResponseEntity<SimulatorDTO>> events(@PathVariable String id, @RequestParam Simulator.Event event) {

        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        var message = MessageBuilder.withPayload(event).build();
        stateMachine.sendEvent(Mono.just(message)).subscribe();

        var currentState = stateMachine.getState().getId().toString();
        var name = (Schema) stateMachine.getExtendedState().getVariables().get("schema");


        return Mono.just(ResponseEntity.ok(new SimulatorDTO(id, name.getName(), currentState)));

    }


    @GetMapping("/create/{schemaId}")
    public Mono<ResponseEntity<SimulatorDTO>> create(@PathVariable String schemaId){

        if (stateMachines.containsKey(schemaId)) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return schemaService.getById(schemaId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Schema not found")))
                .map(
                        schema ->
                        {
                            try {
                                StateMachine<Simulator.State, Simulator.Event> stateMachine = simulator.createStateMachine();

                                stateMachine.getExtendedState().getVariables().put("schema", schema);
                                stateMachine.getExtendedState().getVariables().put("mqtt", new MqttConnector());
                                stateMachine.getExtendedState().getVariables().put("amqp", new AmqpConnector());
                                stateMachine.getExtendedState().getVariables().put("isRunning", false);

                                stateMachines.put(schemaId, stateMachine);

                                return ResponseEntity.ok(new SimulatorDTO(schemaId, schema.getName(), stateMachine.getState().getId().toString()));
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().build();
                            }
                        }
                );

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
            var state = value.getState();
            var name = (Schema) value.getExtendedState().getVariables().get("schema");
            containers.add(new SimulatorDTO(name.getId(), name.getName(), state.getId().toString()));
        });

        return Mono.just(ResponseEntity.ok(containers));
    }


    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<SimulatorDTO>> getContainers() {
        return Flux.interval(Duration.ofSeconds(2)).map(tick -> {
            final List<SimulatorDTO> containers = new ArrayList<>();
            stateMachines.forEach((key, value) -> {
                var state = value.getState();
                var name = (Schema) value.getExtendedState().getVariables().get("schema");
                containers.add(new SimulatorDTO(name.getId(), name.getName(), state.getId().toString()));
            });
            return containers;
        });
    }

    @GetMapping(value = "/stream/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getLogs(@PathVariable String id) {
        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Flux.error(new IllegalArgumentException("Container not found"));
        }

        return Flux.interval(Duration.ofSeconds(2)).map(tick -> (String) stateMachine.getExtendedState().getVariables().get("log"));
    }

}

