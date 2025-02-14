package com.smartuis.api.config.container;

import com.samskivert.mustache.Mustache;
import com.smartuis.shared.schema.Schema;
import lombok.Getter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.*;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Configuration
@EnableStateMachineFactory
public class Container extends StateMachineConfigurerAdapter<ContainerState, ContainerEvent> {

    private Disposable disposable;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void configure(StateMachineConfigurationConfigurer<ContainerState, ContainerEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true)
                .machineId(UUID.randomUUID().toString());

    }

    @Override
    public void configure(StateMachineStateConfigurer<ContainerState, ContainerEvent> states) throws Exception {
        states.withStates()
                .initial(ContainerState.CREATED, created())
                .state(ContainerState.RUNNING, running())
                .state(ContainerState.STOPPED, stopped())
                .state(ContainerState.KILLED, killed());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ContainerState, ContainerEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(ContainerState.CREATED).target(ContainerState.RUNNING).event(ContainerEvent.START)
                .and().withExternal()
                .source(ContainerState.RUNNING).target(ContainerState.STOPPED).event(ContainerEvent.STOP)
                .and().withExternal()
                .source(ContainerState.STOPPED).target(ContainerState.RUNNING).event(ContainerEvent.START)
                .and().withExternal()
                .source(ContainerState.RUNNING).target(ContainerState.KILLED).event(ContainerEvent.KILL)
                .and().withExternal()
                .source(ContainerState.STOPPED).target(ContainerState.KILLED).event(ContainerEvent.KILL);

    }

    private Action<ContainerState, ContainerEvent> running() {
        return stateContext -> {
            Schema simulationSchema = (Schema) stateContext.getExtendedState().getVariables().get("schema");
            if (simulationSchema != null) {

                if (!isRunning.compareAndSet(false, true)) {
                    return;
                }

                disposable = simulationSchema
                        .schema()
                        .getSampler()
                        .sample()
                        .takeWhile(interval -> isRunning.get())
                        .doOnNext(interval -> {
                            Map<String, Object> map = simulationSchema.schema().generate();

                            String template = Optional.ofNullable(simulationSchema.schema().getTemplate()).orElse("");

                            try {
                                String formatted = Mustache.compiler().compile(template).execute(map);
                                log.info(template.isEmpty() ? map.toString() : formatted);
                            }catch (Exception e){
                                log.error("Error while formatting template: {}", e.getMessage());
                            }



                        }).doOnComplete(() -> {
                            isRunning.set(false);
                            stateContext.getStateMachine()
                                    .sendEvent(createMessage(ContainerEvent.STOP))
                                    .subscribe()
                                    .dispose();
                        }).doOnError(error -> {
                            isRunning.set(false);
                            stateContext.getStateMachine()
                                    .sendEvent(createMessage(ContainerEvent.KILL))
                                    .subscribe()
                                    .dispose();
                        }).subscribe();
            }
        };
    }


    private Action<ContainerState, ContainerEvent> stopped() {
        return stateContext -> {
            isRunning.set(false);
            log.info("Simulation stopped");
        };
    }

    private Action<ContainerState, ContainerEvent> killed() {
        return stateContext -> {
            if (isRunning.compareAndSet(true, false)) {
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
                log.info("Simulation Killed");
            }
        };
    }

    private Action<ContainerState, ContainerEvent> created() {
        return stateContext -> log.info("Container created!");
    }

    private Mono<Message<ContainerEvent>> createMessage(ContainerEvent event) {
        return Mono.just(MessageBuilder.withPayload(event).build());
    }


}
