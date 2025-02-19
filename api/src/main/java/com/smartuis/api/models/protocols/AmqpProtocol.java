package com.smartuis.api.models.protocols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smartuis.api.models.interfaces.IProtocol;

public record AmqpProtocol(
        String host,
        Integer port,
        String username,
        String password,
        String exchange,
        String routingKey
) implements IProtocol {

    @JsonCreator
    public AmqpProtocol(
            @JsonProperty("host") String host,
            @JsonProperty("port") Integer port,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("exchange") String exchange,
            @JsonProperty("routingKey") String routingKey
    ) {

        if (port == null || port < 1) {
            throw new IllegalArgumentException("mqtt protocol requires port");
        }

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("mqtt protocol requires host");
        }

        if (exchange == null || exchange.isBlank()) {
            throw new IllegalArgumentException("mqtt protocol requires topic");
        }

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public String type() {
        return "amqp";
    }
}
