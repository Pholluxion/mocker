package com.smartuis.server.service.impl;

import com.smartuis.server.models.protocols.MqttProtocol;
import com.smartuis.server.repository.SchemaRepository;
import com.smartuis.server.models.schema.Schema;
import com.smartuis.server.repository.SimulationRepository;
import com.smartuis.server.service.SchemaService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class SchemaServiceImpl implements SchemaService {

    private final SchemaRepository schemaRepository;

    private final SimulationRepository simulationRepository;

    public SchemaServiceImpl(SchemaRepository schemaRepository, SimulationRepository simulationRepository) {
        this.schemaRepository = schemaRepository;
        this.simulationRepository = simulationRepository;
    }

    @Override
    public Mono<Schema> create(Schema schema) {
        return isNameValid(schema.getName())
                .flatMap(isValid -> {
                    if (Boolean.FALSE.equals(isValid)) {
                        return Mono.error(new IllegalArgumentException("Schema with name " + schema.getName() + " already exists"));
                    }
                    return isClientMqttValid(schema)
                            .flatMap(isMqttValid -> {
                                if (Boolean.TRUE.equals(isMqttValid)) {
                                    return Mono.error(new IllegalArgumentException("Schema with mqtt client id already exists"));
                                }
                                return schemaRepository.save(schema);
                            });
                });
    }

    @Override
    public Mono<Schema> getById(String id) {
        return schemaRepository.findById(id);
    }

    @Override
    public Flux<Schema> findAll() {
        return schemaRepository.findAll();
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return schemaRepository.findById(id)
                .flatMap(schemaRepository::delete)
                .map(aBoolean -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> existsSimulation(String id) {
        return simulationRepository.exists(id);
    }

    @Override
    public Mono<Schema> template(String id, String template) {
        return schemaRepository.findById(id)
                .map(schema -> {
                    schema.setTemplate(template);
                    return schema;
                })
                .flatMap(schemaRepository::save);
    }

   @Override
    public Mono<Boolean> isClientMqttValid(Schema schema) {
        var newClientId = ((MqttProtocol) schema.getProtocolByType("mqtt")).clientId();
        return findAll().any(s -> newClientId.equals(((MqttProtocol) s.getProtocolByType("mqtt")).clientId()));
    }

    @Override
    public Mono<Boolean> isNameValid(String name) {
        return schemaRepository.findByName(name)
                .map(schema -> false)
                .defaultIfEmpty(true);
    }


}
