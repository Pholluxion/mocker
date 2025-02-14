package com.smartuis.api.config.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


@Slf4j
public class MqttConnector {

    private IMqttAsyncClient client;

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void connect(String host, int port, String clientId, String username, String password) {
        try {
            String brokerUri = String.format("tcp://%s:%d", host, port);

            client = new MqttAsyncClient(brokerUri, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);

            client.connect(options).waitForCompletion();
            log.info("✅ Successfully connected to the MQTT broker at " + brokerUri);

        } catch (MqttException e) {
            log.error("❌ Error on connection to the MQTT broker:");
            log.error("   - Message: " + e.getMessage());
            log.error("   - Error code: " + e.getReasonCode());
            log.error("   - Cause: " + e.getCause());
            e.printStackTrace();
        }

    }


    public void publish(String topic, String payload, int qos, boolean retained) {
        try {
            if (!isConnected()) {
                log.error("⚠️ The client is not connected to the broker MQTT.");
                return;
            }

            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            message.setRetained(retained);

            client.publish(topic, message);

            log.info("✅ Message published in the topic \"" + topic + "\": " + payload);

        } catch (MqttException e) {
            log.error("❌ Error on connection to the MQTT broker:");
            log.error("   - Message: " + e.getMessage());
            log.error("   - Error code: " + e.getReasonCode());
            log.error("   - Cause: " + e.getCause());
            e.printStackTrace();
        }
    }


}
