package com.smartuis.server.simulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;

import com.smartuis.server.config.amqp.AmqpConnector;
import com.smartuis.server.config.mqtt.MqttConnector;
import com.smartuis.server.models.protocols.AmqpProtocol;
import com.smartuis.server.models.protocols.MqttProtocol;
import com.smartuis.server.models.schema.Schema;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
public class Simulator {

    public enum State {CREATED, RUNNING, STOPPED, KILLED}

    public enum Event {START, STOP, KILL}

    private final MqttConnector mqttConnector;

    private final AmqpConnector amqpConnector;

    private final AtomicBoolean isRunning;

    private final Schema schema;

    private final StateMachine<State, Event> stateMachine;

    private Disposable disposable;

    public Simulator(Schema schema) throws Exception {
        this.schema = schema;
        this.isRunning = new AtomicBoolean(false);
        this.mqttConnector = new MqttConnector();
        this.amqpConnector = new AmqpConnector();
        this.stateMachine = createStateMachine();
    }

    public State getState() {
        return stateMachine.getState().getId();
    }


    public StateMachine<State, Event> createStateMachine() throws Exception {
        StateMachineBuilder.Builder<State, Event> builder = StateMachineBuilder.builder();

        builder.configureConfiguration()
                .withConfiguration()
                .machineId(UUID.randomUUID().toString())
                .autoStartup(true);

        builder.configureStates()
                .withStates()
                .initial(State.CREATED)
                .state(State.RUNNING, runningAction())
                .state(State.STOPPED, stoppedAction())
                .state(State.KILLED, killedAction());

        builder.configureTransitions()
                .withExternal()
                .source(State.CREATED).target(State.RUNNING).event(Event.START)
                .and()
                .withExternal().source(State.RUNNING).target(State.STOPPED).event(Event.STOP)
                .and().
                withExternal().source(State.STOPPED).target(State.RUNNING).event(Event.START)
                .and()
                .withExternal().source(State.RUNNING).target(State.KILLED).event(Event.KILL)
                .and()
                .withExternal().source(State.STOPPED).target(State.KILLED).event(Event.KILL);

        return builder.build();

    }


    private Action<State, Event> runningAction() {

        return context -> {

            if (isRunning.get()) {
                log.warn("Simulation is already running.");
                return;
            }

            try {
                setupProtocols(context);
                startSimulation(context);
            } catch (Exception e) {
                handleError(context, e);
            }
        };
    }

    private Action<State, Event> stoppedAction() {
        return this::stopSimulation;
    }

    private Action<State, Event> killedAction() {
        return this::stopSimulation;
    }


    private void startSimulation(StateContext<State, Event> context) {

        isRunning.set(true);

        disposable = schema.getSampler()
                .sample()
                .takeWhile(interval -> isRunning.get())
                .doOnNext(interval -> publishMessage(context))
                .doOnComplete(() -> stopSimulation(context))
                .doOnError(error -> handleError(context, error))
                .subscribe();

    }

    private void stopSimulation(StateContext<State, Event> context) {

        if (isRunning.get()) {

            disposable.dispose();

            if (mqttConnector.isConnected()) {
                mqttConnector.disconnect();
            }

            if (amqpConnector.isConnected()) {
                amqpConnector.disconnect();
            }

            isRunning.set(false);
            disposable = null;

        } else {
            log.warn("Simulation is not running.");
        }
    }

    private void setupProtocols(StateContext<State, Event> context) {


        MqttProtocol mqttProtocol = (MqttProtocol) schema.getProtocolByType("mqtt");
        AmqpProtocol amqpProtocol = (AmqpProtocol) schema.getProtocolByType("amqp");

        if (mqttProtocol != null) {
            mqttConnector.connect(mqttProtocol);
        }

        if (amqpProtocol != null) {
            amqpConnector.connect(amqpProtocol);
        }

        if (mqttProtocol == null && amqpProtocol == null) {
            handleError(context, new Exception("No protocols found."));
        }

    }


    private void publishMessage(StateContext<State, Event> context) {


        try {
            var message = processSample();

            if (mqttConnector.isConnected()) {
                mqttConnector.publish(message);
            }

            if (amqpConnector.isConnected()) {
                amqpConnector.sendMessage(message);
            }

        } catch (Exception e) {
            handleError(context, e);
        }
    }


    private String processSample() {

        Map<String, Object> data = schema.generate();
        String template = schema.getTemplate();

        return (template == null) ?
                data.toString() :
                Mustache.compiler()
                        .compile(template)
                        .execute(data);


    }


    private void handleError(StateContext<State, Event> context, Throwable error) {
        log.error("Error occurred: {}", error.getMessage());
        var message = MessageBuilder.withPayload(Event.STOP).build();
        context.getStateMachine()
                .sendEvent(Mono.just(message))
                .subscribe();
    }


}
