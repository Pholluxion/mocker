package com.smartuis.api.repository;

import com.smartuis.api.config.amqp.AmqpConnector;
import com.smartuis.api.config.mqtt.MqttConnector;
import com.smartuis.api.dtos.SimulationDTO;
import com.smartuis.api.models.schema.Schema;
import com.smartuis.api.simulator.Simulator;
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

    private final Simulator simulator;
    private final Map<String, StateMachine<Simulator.State, Simulator.Event>> stateMachines;

    public SimulationRepository(Simulator simulator) {
        this.simulator = simulator;
        this.stateMachines = new HashMap<>();
    }

    public Mono<SimulationDTO> create(Schema schema) {
        try {
            var stateMachine = simulator.createStateMachine();
            var map = new HashMap<>();

            map.put("schema", schema);
            map.put("isRunning", false);
            map.put("name", schema.getName());
            map.put("mqtt", new MqttConnector());
            map.put("amqp", new AmqpConnector());

            stateMachine.getExtendedState().getVariables().putAll(map);
            stateMachines.putIfAbsent(schema.getId(), stateMachine);

            var simulationDTO = new SimulationDTO(
                    schema.getId(),
                    schema.getName(),
                    stateMachine.getState().getId().name()
            );

            return Mono.just(simulationDTO);

        } catch (Exception e) {
            return Mono.empty();
        }
    }

    public Flux<SimulationDTO> findAll() {
        return Flux.fromIterable(stateMachines.entrySet())
                .map(entry -> {
                    var id = entry.getKey();
                    var state = entry.getValue().getState().getId().name();
                    var variables = getVariables(entry.getValue());
                    var name = (String) variables.get("name");
                    return new SimulationDTO(id, name, state);
                });
    }

    public Mono<SimulationDTO> getById(String id) {
        return Mono.justOrEmpty(stateMachines.get(id))
                .map(stateMachine -> {
                    var state = stateMachine.getState().getId().name();
                    var variables = getVariables(stateMachine);
                    var name = (String) variables.get("name");
                    return new SimulationDTO(id, name, state);
                });
    }

    public Mono<Boolean> delete(String id) {
        return Mono.justOrEmpty(stateMachines.remove(id))
                .map(stateMachine -> {
                    stateMachine.stopReactively();
                    return true;
                });
    }


    public Flux<String> logs(String id) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> {
                    var stateMachine = stateMachines.get(id);

                    if (stateMachine == null) {
                        return "";
                    }

                    var variables = getVariables(stateMachine);
                    var log = (String) variables.get("log");

                    if (log == null) {
                        return "";
                    }

                    return (String) variables.get("log");
                });
    }

    public Mono<SimulationDTO> handleEvent(String id, Simulator.Event event) {
        var stateMachine = stateMachines.get(id);

        if (stateMachine == null) {
            return Mono.empty();
        }

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).subscribe();

        var name = (String) getVariables(stateMachine).get("name");
        var currentState = stateMachine.getState().getId().name();

        if (event == Simulator.Event.KILL) {
            stateMachine.stopReactively();
            stateMachines.remove(id);
        }

        return Mono.just(new SimulationDTO(id, name, currentState));
    }

    private Map<Object, Object> getVariables(StateMachine<Simulator.State, Simulator.Event> stateMachine) {
        return stateMachine.getExtendedState().getVariables();
    }


}
