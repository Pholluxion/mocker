package com.smartuis.server.service;

import com.smartuis.server.models.schema.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SchemaService {

    Mono<Schema> create(Schema schema);

    Mono<Schema> getById(String id);

    Flux<Schema> findAll();

    Mono<Schema> template(String id, String template);

    Mono<Boolean> delete(String id);

    Mono<Boolean> existsSimulation(String id);

}
