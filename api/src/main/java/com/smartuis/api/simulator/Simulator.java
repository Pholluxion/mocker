package com.smartuis.api.simulator;

import com.smartuis.api.models.schema.Schema;
import com.smartuis.api.service.SimulationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class Simulator extends StateMachineConfigurerAdapter<Simulator.State, Simulator.Event> {

    public enum State { CREATED, RUNNING, STOPPED, KILLED }
    public enum Event { START, STOP, KILL }

    private final SimulationService simulationService = new SimulationService();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);


    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.CREATED)
                .state(State.RUNNING, running())
                .state(State.STOPPED, stopped())
                .state(State.KILLED, killed());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal()
                .source(State.CREATED).target(State.RUNNING).event(Event.START)
                .and().withExternal()
                .source(State.RUNNING).target(State.STOPPED).event(Event.STOP)
                .and().withExternal()
                .source(State.STOPPED).target(State.RUNNING).event(Event.START)
                .and().withExternal()
                .source(State.RUNNING).target(State.KILLED).event(Event.KILL)
                .and().withExternal()
                .source(State.STOPPED).target(State.KILLED).event(Event.KILL);
    }

    private Action<State, Event> running() {
        return stateContext -> {
            if (!isRunning.compareAndSet(false, true)) return;

            try {

                var schema = (Schema) stateContext.getExtendedState().getVariables().get("schema");

                simulationService.startSimulation(schema);
            } catch (Exception e) {
                log.error("Error while starting simulation: {}", e.getMessage());
                handleError(stateContext, e);
            }
        };
    }

    private Action<State, Event> stopped() {
        return stateContext -> {
            isRunning.set(false);
            simulationService.stopSimulation();
            log.info("Simulation stopped.");
        };
    }

    private Action<State, Event> killed() {
        return stateContext -> {
            isRunning.set(false);
            simulationService.stopSimulation();
            log.info("Simulation killed.");
        };
    }

    private void handleError(StateContext<State, Event> stateContext, Throwable error) {
        log.error("Error occurred: {}", error.getMessage());
        var message = MessageBuilder.withPayload(Event.STOP).build();
        stateContext.getStateMachine().sendEvent(Mono.just(message)).subscribe();
    }
}

