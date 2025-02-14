package com.smartuis.shared.protocols;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.smartuis.shared.interfaces.IProtocol;

@JsonTypeName("mqtt")
public record MqttProtocol(
        String host,
        Integer port,
        String username,
        String password,
        String clientId,
        String topic,
        Integer qos

) implements IProtocol {

}
