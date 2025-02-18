package com.smartuis.api.config.mqtt;

import com.smartuis.api.models.protocols.MqttProtocol;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

@Slf4j
public class MqttConnector {

    private MqttProtocol protocol;
    private IMqttAsyncClient client;

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

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
                    log.warn("⚠️ Connection lost with the MQTT broker. Cause: {}", cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    log.info("📩 Message received on topic '{}': {}", topic, new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("✅ Message delivery complete.");
                }
            });

            client.connect(options).waitForCompletion();
            log.info("✅ Successfully connected to the MQTT broker at {} - client {}", brokerUri, this.protocol.clientId());
        } catch (MqttException e) {
            printError(e);
        }

    }

    @PreDestroy
    public void disconnect() {
        if (isConnected()) {
            try {
                client.disconnect().waitForCompletion();
                log.info("✅ Successfully disconnected from the MQTT broker.");
            } catch (MqttException e) {
                printError(e);
            }
        }
    }

    public void publish(String payload) {
        if (!isConnected()) {
            throw new IllegalStateException("The client is not connected to the broker MQTT.");
        }

        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        message.setRetained(false);

        try {
            client.publish(this.protocol.topic(), message);
            log.info("✅ Message published in the topic '{}': {}", this.protocol.topic(), payload);
        } catch (MqttException e) {
            throw new RuntimeException("Failed to publish the message", e);
        }
    }


    private void printError(MqttException e) {
        log.error("❌ Error on connection to the MQTT broker:");
        log.error("   - Message: {}", e.getMessage());
        log.error("   - Error code: {}", e.getReasonCode());
        log.error("   - Cause: {}", String.valueOf(e.getCause()));
    }
}
