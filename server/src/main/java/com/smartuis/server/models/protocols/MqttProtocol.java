package com.smartuis.server.models.protocols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smartuis.server.models.interfaces.IProtocol;

import java.util.UUID;

public record MqttProtocol(
        String host,
        Integer port,
        String topic,
        String clientId,
        String username,
        String password
) implements IProtocol {

    @JsonCreator
    public MqttProtocol(
            @JsonProperty("host") String host,
            @JsonProperty("port") Integer port,
            @JsonProperty("topic") String topic,
            @JsonProperty("clientId") String clientId,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password
    ) {
        if (port == null || port < 1) {
            throw new IllegalArgumentException("mqtt protocol requires port");
        }

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("mqtt protocol requires host");
        }

        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("mqtt protocol requires topic");
        }

        if (clientId == null || clientId.isBlank()) {
            this.clientId = UUID.randomUUID()
                    .toString()
                    .replace("-", "");
        } else {
            this.clientId = clientId;
        }

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.topic = topic;
    }


    @Override
    public String type() {
        return "mqtt";
    }
}
