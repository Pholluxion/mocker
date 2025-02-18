package com.smartuis.api.models.protocols;

import com.smartuis.api.models.interfaces.IProtocol;

public record AmqpProtocol(
        String host,
        Integer port,
        String username,
        String password,
        String exchange,
        String routingKey
) implements IProtocol {
    @Override
    public String type() {
        return "amqp";
    }
}
