package com.smartuis.api.config.mqtt;

import com.smartuis.api.models.protocols.MqttProtocol;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


@Slf4j
public class MqttConnector {

    private String topic;
    private IMqttAsyncClient client;

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void connect(MqttProtocol protocol) {
        try {

            if (isConnected()) {
                return;
            }

            String brokerUri = String.format("tcp://%s:%d", protocol.host(), protocol.port());

            client = new MqttAsyncClient(brokerUri, protocol.clientId(), new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(protocol.username());
            options.setPassword(protocol.password().toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);

            topic = protocol.topic();

            client.connect(options).waitForCompletion();
            log.info("✅ Successfully connected to the MQTT broker at {}", brokerUri);

        } catch (MqttException e) {
            printError(e);
        }

    }


    public void publish( String payload) {
        try {
            if (!isConnected()) {
                log.error("⚠️ The client is not connected to the broker MQTT.");
                return;
            }

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            message.setRetained(true);

            client.publish(topic, message);

            log.info("✅ Message published in the topic \"{}\": {}", topic, payload);

        } catch (MqttException e) {
            printError(e);
        }
    }

    private void printError(MqttException e) {
        log.error("❌ Error on connection to the MQTT broker:");
        log.error("   - Message: {}", e.getMessage());
        log.error("   - Error code: {}", e.getReasonCode());
        log.error("   - Cause: {}", String.valueOf(e.getCause()));
    }

}
