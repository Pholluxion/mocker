package com.smartuis.api.rest;

import com.smartuis.api.config.container.ContainerEvent;
import com.smartuis.api.config.container.ContainerState;
import com.smartuis.api.dtos.ContainerDTO;
import com.smartuis.api.service.SchemaService;
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
public class ContainerResource {

    private final SchemaService schemaService;
    private final StateMachineFactory<ContainerState, ContainerEvent> stateMachineFactory;
    private final Map<String, StateMachine<ContainerState, ContainerEvent>> stateMachines;


    public ContainerResource(SchemaService schemaService, StateMachineFactory<ContainerState, ContainerEvent> stateMachineFactory) {
        this.schemaService = schemaService;
        this.stateMachineFactory = stateMachineFactory;
        this.stateMachines = new HashMap<>();
    }

    @PostConstruct
    private void createContainers(){
        var schemas = schemaService.getSchemas();
        schemas.forEach(schemaWrapper -> {
            var stateMachine = stateMachineFactory.getStateMachine();
            stateMachine.getExtendedState().getVariables().put("schema", schemaWrapper);
            stateMachines.put(schemaWrapper.id(), stateMachine);
        });
    }


    @PostMapping("/event/{name}")
    public Flux<ContainerEvent> events(@RequestBody ContainerEvent event, @PathVariable String name) {

        var stateMachine = stateMachines.get(name);

        var message = MessageBuilder.withPayload(event).build();
        return stateMachine
                .sendEvent(Mono.just(message))
                .map(s -> s.getMessage().getPayload());


    }


    @GetMapping("/create/{name}")
    public Mono<ResponseEntity<Void>> createSchema(@PathVariable String name) {

        var schema = schemaService.getSchema(name);

        if (schema == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        var stateMachine = stateMachineFactory.getStateMachine();

        stateMachine.getExtendedState().getVariables().put("schema", schema);

        stateMachines.put(name, stateMachine);


        return stateMachine.startReactively().map(s -> ResponseEntity.ok().build());
    }

    @GetMapping(value = "/state/{name}")
    public Mono<ContainerState> getCurrentState(@PathVariable String name) {

        var stateMachine = stateMachines.get(name);

        return Mono.just(Objects.requireNonNull(stateMachine.getState()).getId());
    }


    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<ContainerDTO>> getContainers() {
        return Flux.interval(Duration.ofSeconds(5)).map(tick -> {
            final List<ContainerDTO> containers = new ArrayList<>();
            stateMachines.forEach((key, value) -> {
                var id = value.getId();
                var state = value.getState();
                containers.add(new ContainerDTO(id, key, state.getId().toString()));
            });
            return containers;
        });
    }
}
