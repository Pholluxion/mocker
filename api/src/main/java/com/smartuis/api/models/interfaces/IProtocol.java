package com.smartuis.api.models.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.smartuis.api.models.protocols.MqttProtocol;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = MqttProtocol.class, name = "mqtt"),})
public interface IProtocol {
    String type();
}
