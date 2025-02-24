package com.smartuis.server.models.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.smartuis.server.models.protocols.AmqpProtocol;
import com.smartuis.server.models.protocols.MqttProtocol;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MqttProtocol.class, name = "mqtt"),
        @JsonSubTypes.Type(value = AmqpProtocol.class, name = "amqp")
})
public interface IProtocol {
    String type();
}
