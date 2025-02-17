package com.smartuis.api.models.protocols;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.api.models.constraints.protocols.MqttValidator;
import com.smartuis.api.models.interfaces.IProtocol;

@JsonTypeName("mqtt")
@MqttValidator
public record MqttProtocol(
        String host,
        Integer port,
        String username,
        String password,
        String clientId,
        String topic,
        Integer qos
) implements IProtocol {

    @Override
    public String type() {
        return "mqtt";
    }
}
