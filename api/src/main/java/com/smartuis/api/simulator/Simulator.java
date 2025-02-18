package com.smartuis.api.simulator;

import com.samskivert.mustache.Mustache;
import com.smartuis.api.config.amqp.AmqpConnector;
import com.smartuis.api.config.mqtt.MqttConnector;
import com.smartuis.api.models.protocols.AmqpProtocol;
import com.smartuis.api.models.protocols.MqttProtocol;
import com.smartuis.api.models.schema.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@EnableStateMachineFactory
@Slf4j
public class Simulator extends StateMachineConfigurerAdapter<Simulator.State, Simulator.Event> {

    public enum State {CREATED, RUNNING, STOPPED, KILLED}

    public enum Event {START, STOP, KILL}

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration()
                .machineId(UUID.randomUUID().toString())
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.CREATED)
                .state(State.RUNNING, runningAction())
                .state(State.STOPPED, stoppedAction())
                .state(State.KILLED, killedAction());
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

    private Action<State, Event> runningAction() {


        return context -> {

            Map<Object, Object> variables = context.getExtendedState().getVariables();

            if (variables.get("isRunning") != null && (boolean) variables.get("isRunning")) {
                log.warn("Simulation is already running.");
                return;
            }

            try {
                Schema schema = (Schema) variables.get("schema");

                if (schema == null) {
                    log.error("Schema not provided. Cannot start simulation.");
                    sendEvent(context, Event.STOP);
                    return;
                }

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

    private void setupProtocols(StateContext<State, Event> context) {

        Map<Object, Object> variables = context.getExtendedState().getVariables();

        Schema schema = (Schema) variables.get("schema");

        MqttConnector mqttConnector = (MqttConnector) variables.get("mqtt");
        AmqpConnector amqpConnector = (AmqpConnector) variables.get("amqp");

        MqttProtocol mqttProtocol = (MqttProtocol) schema.getProtocolByType("mqtt");
        AmqpProtocol amqpProtocol = (AmqpProtocol) schema.getProtocolByType("amqp");

        if (mqttProtocol != null) {
            mqttConnector.connect(mqttProtocol);
        }

        if (amqpProtocol != null) {
            amqpConnector.connect(amqpProtocol);
        }

        if (mqttProtocol == null && amqpProtocol == null) {
            throw new IllegalArgumentException("No valid protocol found. Cannot start simulation.");
        }
    }

    private void startSimulation(StateContext<State, Event> context) {

        Map<Object, Object> variables = context.getExtendedState().getVariables();

        variables.put("isRunning", true);

        Boolean isRunning = (Boolean) variables.get("isRunning");
        Schema schema = (Schema) variables.get("schema");


        var disposable = schema.getSampler()
                .sample()
                .takeWhile(interval -> Boolean.TRUE.equals(isRunning))
                .doOnNext(interval -> processSample(context))
                .doOnComplete(() -> stopSimulation(context))
                .doOnError((error) -> handleError(context, error))
                .subscribe();

        variables.put("disposable", disposable);
    }

    private void stopSimulation(StateContext<State, Event> context) {
        Map<Object, Object> variables = context.getExtendedState().getVariables();

        Boolean isRunning = (Boolean) variables.get("isRunning");
        Disposable disposable = (Disposable) variables.get("disposable");

        if (Boolean.TRUE.equals(isRunning)) {

            disposable.dispose();

            variables.put("isRunning", false);
            variables.put("disposable", null);

        } else {
            log.warn("Simulation is not running.");
        }
    }

    private void processSample(StateContext<State, Event> context) {
        try {

            Map<Object, Object> variables = context.getExtendedState().getVariables();

            Schema schema = (Schema) variables.get("schema");

            Map<String, Object> data = schema.generate();
            Object template = schema.getTemplate();

            String message = (template == null)
                    ? data.toString()
                    : Mustache.compiler().compile(template.toString()).execute(data);

            context.getExtendedState().getVariables().put("log", message);

            publishMessage(message, context);
        } catch (Exception e) {
            log.error("Error processing sample: {}", e.getMessage());
        }
    }

    private void publishMessage(String message, StateContext<State, Event> context) {
        try {

            Map<Object, Object> variables = context.getExtendedState().getVariables();

            MqttConnector mqttConnector = (MqttConnector) variables.get("mqtt");
            AmqpConnector amqpConnector = (AmqpConnector) variables.get("amqp");

            if (mqttConnector.isConnected()) {
                mqttConnector.publish(message);
            }

            if (amqpConnector.isConnected()) {
                amqpConnector.sendMessage(message);
            }

        } catch (Exception e) {
            log.error("Failed to publish message: {}", e.getMessage());
        }
    }


    private void handleError(StateContext<State, Event> context, Throwable error) {
        log.error("Error occurred: {}", error.getMessage());
        sendEvent(context, Event.STOP);
    }

    private void sendEvent(StateContext<State, Event> context, Event event) {
        context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).subscribe();
    }

}
