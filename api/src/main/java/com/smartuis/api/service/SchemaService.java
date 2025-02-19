package com.smartuis.api.service;

import com.smartuis.api.models.schema.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SchemaService {

    Mono<Schema> create(Schema schema);

    Mono<Schema> getById(String id);

    Flux<Schema> findAll();

    Mono<Schema> template(String id, Object template);

    Mono<Boolean> delete(String id);

    Mono<Boolean> existsByName(String id);

}
