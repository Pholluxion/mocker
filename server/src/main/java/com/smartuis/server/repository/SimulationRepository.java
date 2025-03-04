package com.smartuis.server.repository;

import com.smartuis.server.dtos.SimulationDTO;
import com.smartuis.server.models.schema.Schema;
import com.smartuis.server.simulator.Simulator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SimulationRepository {

    private final Map<String, Simulator> simulatorMap;

    public SimulationRepository() {
        this.simulatorMap = new HashMap<>();
    }

    public Mono<SimulationDTO> create(Schema schema) {
        try {

            var simulator = new Simulator(schema);

            simulatorMap.putIfAbsent(schema.getId(), simulator);

            var simulationDTO = new SimulationDTO(
                    schema.getId(),
                    schema.getName(),
                    simulator.getState().name()
            );

            return Mono.just(simulationDTO);

        } catch (Exception e) {
            return Mono.empty();
        }
    }

    public Flux<SimulationDTO> findAll() {
        return Flux.fromIterable(simulatorMap.entrySet())
                .map(entry -> {
                    var id = entry.getKey();
                    var state = entry.getValue().getState().name();
                    var name = entry.getValue().getSchema().getName();
                    return new SimulationDTO(id, name, state);
                });
    }

    public Mono<SimulationDTO> getById(String id) {
        return Mono.justOrEmpty(simulatorMap.get(id))
                .map(simulator -> {
                    var state = simulator.getState().name();
                    var name = simulator.getSchema().getName();
                    return new SimulationDTO(id, name, state);
                });
    }

    public Mono<Boolean> delete(String id) {
        return Mono.justOrEmpty(simulatorMap.remove(id))
                .map(simulator -> {
                    simulator.getStateMachine().stopReactively();
                    return true;
                });
    }

    public Mono<Boolean> exists(String id) {
        return Mono.just(simulatorMap.containsKey(id));
    }


    public Flux<String> logs(String id, int interval) {
        return Flux.interval(Duration.ofSeconds(interval))
                .map(i -> {
                    var simulator = simulatorMap.get(id);

                    var log = (String) getVariables(simulator.getStateMachine()).get("log");

                    return log != null ? log : "";

                });
    }

    public Mono<SimulationDTO> handleEvent(String id, Simulator.Event event) {
        var simulator = simulatorMap.get(id);

        if (simulator == null) {
            return Mono.empty();
        }

        simulator.getStateMachine()
                .sendEvent(Mono.just(MessageBuilder.withPayload(event).build()))
                .subscribe();

        var name = simulator.getSchema().getName();
        var currentState = simulator.getState().name();

        if (event == Simulator.Event.KILL) {
            simulator.getStateMachine().stopReactively();
            simulatorMap.remove(id);
        }

        return Mono.just(new SimulationDTO(id, name, currentState));
    }

    private Map<Object, Object> getVariables(StateMachine<Simulator.State, Simulator.Event> stateMachine) {
        return stateMachine
                .getExtendedState()
                .getVariables();
    }


}
