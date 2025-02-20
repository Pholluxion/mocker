package com.smartuis.api.repository;

import com.smartuis.api.models.schema.Schema;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SchemaRepository extends ReactiveMongoRepository<Schema, String> {
    Mono<Schema> findByName(String name);

}
