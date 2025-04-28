package com.smartuis.server.models.protocols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.rabbitmq.client.BuiltinExchangeType;
import com.smartuis.server.models.interfaces.IProtocol;

import java.util.Arrays;

public record AmqpProtocol(
        String host,
        Integer port,
        String username,
        String password,
        String exchange,
        String routingKey,
        String exchangeType
) implements IProtocol {

    public static final String[] EXCHANGE_TYPES = {
            BuiltinExchangeType.DIRECT.getType(),
            BuiltinExchangeType.FANOUT.getType(),
            BuiltinExchangeType.HEADERS.getType(),
            BuiltinExchangeType.TOPIC.getType(),
    };

    @JsonCreator
    public AmqpProtocol(
            @JsonProperty("host") String host,
            @JsonProperty("port") Integer port,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("exchange") String exchange,
            @JsonProperty("routingKey") String routingKey,
            @JsonProperty("exchangeType") String exchangeType
    ) {

        if (port == null || port < 1) {
            throw new IllegalArgumentException("amqp protocol requires port");
        }

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("amqp protocol requires host");
        }

        if (exchange == null || exchange.isBlank()) {
            throw new IllegalArgumentException("amqp protocol requires exchange");
        }

        if ( !Arrays.asList(EXCHANGE_TYPES).contains(exchangeType)) {
            throw new IllegalArgumentException("amqp protocol requires valid exchange type  [" + String.join(", ", EXCHANGE_TYPES) + "]");
        }


        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.exchangeType =  exchangeType == null ? BuiltinExchangeType.FANOUT.getType()  : exchangeType;
    }

    @Override
    public String type() {
        return "amqp";
    }

}
