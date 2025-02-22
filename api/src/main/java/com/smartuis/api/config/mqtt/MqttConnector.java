package com.smartuis.api.config.mqtt;

import com.smartuis.api.models.protocols.MqttProtocol;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MqttConnector is responsible for managing the connection to an MQTT broker.
 * It provides methods to connect, disconnect, and publish messages.
 */
@Slf4j
public class MqttConnector {

    private MqttProtocol protocol;
    private IMqttAsyncClient client;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Checks if the connector is currently connected to the MQTT broker.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    /**
     * Connects to the MQTT broker using the provided MqttProtocol configuration.
     * If already connected, it will return immediately.
     *
     * @param protocol the MqttProtocol configuration to use for the connection
     */
    public void connect(MqttProtocol protocol) {
        try {
            this.protocol = protocol;

            if (isConnected()) {
                return;
            }

            String brokerUri = String.format("tcp://%s:%d", this.protocol.host(), this.protocol.port());
            client = new MqttAsyncClient(brokerUri, this.protocol.clientId(), new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(this.protocol.username());
            options.setPassword(this.protocol.password().toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("Connection lost with the MQTT broker. Cause: {}", cause.getMessage());
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // Do something when a message arrives
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Do something when the message has been delivered
                }
            });

            client.connect(options).waitForCompletion();
            log.info("Successfully connected to the MQTT broker at {} - client {}", brokerUri, this.protocol.clientId());
        } catch (MqttException e) {
            printError(e);
        }
    }

    /**
     * Attempts to reconnect with exponential backoff.
     */
    private void reconnect() {
        scheduler.schedule(() -> {
            log.info("Attempting to reconnect to the MQTT broker...");
            connect(this.protocol);
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * Disconnects from the MQTT broker. This method is annotated with @PreDestroy
     * to ensure it is called when the application context is destroyed.
     */
    @PreDestroy
    public void disconnect() {
        if (isConnected()) {
            try {
                client.disconnect().waitForCompletion();
                log.info("Successfully disconnected from the MQTT broker.");
            } catch (MqttException e) {
                printError(e);
            }
        }
        scheduler.shutdown();
    }

    /**
     * Publishes a message to the MQTT broker. If the client is not connected, it will log a warning.
     *
     * @param payload the message payload to publish
     */
    public void publish(String payload) {
        if (!isConnected()) {
            log.warn("Channel is not open. Attempting to reconnect...");
            reconnect();
            return;
        }

        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        message.setRetained(false);

        try {
            client.publish(this.protocol.topic(), message);
        } catch (MqttException e) {
            printError(e);
        }
    }

    /**
     * Logs detailed error information for an MqttException.
     *
     * @param e the MqttException to log
     */
    private void printError(MqttException e) {
        log.error("❌ Error on connection to the MQTT broker:");
        log.error("   - Message: {}", e.getMessage());
        log.error("   - Error code: {}", e.getReasonCode());
        log.error("   - Cause: {}", String.valueOf(e.getCause()));
    }
}
