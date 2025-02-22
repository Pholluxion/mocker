package com.smartuis.server.repository;

import com.smartuis.server.models.schema.Schema;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SchemaRepository extends ReactiveMongoRepository<Schema, String> {
    Mono<Schema> findByName(String name);

}
