package com.smartuis.api.service;


import com.samskivert.mustache.Mustache;
import com.smartuis.api.config.mqtt.MqttConnector;
import com.smartuis.api.models.protocols.MqttProtocol;
import com.smartuis.api.models.schema.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class SimulationService {

    private final MqttConnector mqttConnector;
    private final AtomicBoolean isRunning ;

    private Disposable disposable;

    public SimulationService() {
        this.mqttConnector = new MqttConnector();
        this.isRunning = new AtomicBoolean(false);
    }

    /**
     * Starts the simulation using the provided schema.
     * @param schema The schema that defines the simulation.
     */
    public void startSimulation(Schema schema) {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("Simulation is already running.");
            return;
        }

        if (schema == null) {
            log.error("Schema is null, cannot start simulation.");
            isRunning.set(false);
            return;
        }

        try {
            log.info("Connecting to MQTT...");
            var mqttProtocol = (MqttProtocol) schema.getProtocolByType("mqtt");
            mqttConnector.connect(mqttProtocol);

            log.info("Starting data sampling...");
            disposable = schema.getSampler()
                    .sample()
                    .takeWhile(interval -> isRunning.get())
                    .doOnNext(interval -> processSample(schema))
                    .doOnComplete(this::onComplete)
                    .doOnError(this::onError)
                    .subscribe();

        } catch (Exception e) {
            log.error("Error while starting simulation: {}", e.getMessage());
            stopSimulation();
        }
    }

    /**
     * Stops the simulation.
     */
    public void stopSimulation() {
        if (isRunning.compareAndSet(true, false)) {
            cleanupResources();
            log.info("Simulation stopped.");
        } else {
            log.warn("Simulation is not running.");
        }
    }

    /**
     * Processes a sample using the provided schema.
     * @param schema The schema that defines the sample.
     */
    private void processSample(Schema schema) {
        try {
            Map<String, Object> data = schema.generate();
            Object template = schema.getTemplate();

            String message = (template == null)
                    ? data.toString()
                    : Mustache.compiler().compile(template.toString()).execute(data);

            publishMessage(message);
        } catch (Exception e) {
            log.error("Error while processing sample: {}", e.getMessage());
        }
    }

    /**
     * Publishes a message to the MQTT broker.
     * @param message The message to publish.
     */
    private void publishMessage(String message) {
        try {
            mqttConnector.publish(message);
            log.info("Published message: {}", message);
        } catch (Exception e) {
            log.error("Failed to publish message: {}", e.getMessage());
        }
    }

    /**
     * Cleans up resources used during the simulation.
     */
    private void cleanupResources() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
            log.info("Resources cleaned up successfully.");
        }
    }

    /**
     * Handles the completion of the simulation.
     */
    private void onComplete() {
        log.info("Simulation completed successfully.");
        stopSimulation();
    }

    /**
     * Handles an error that occurred during the simulation.
     * @param error The error that occurred.
     */
    private void onError(Throwable error) {
        log.error("Error occurred during simulation: {}", error.getMessage());
        stopSimulation();
    }
}
