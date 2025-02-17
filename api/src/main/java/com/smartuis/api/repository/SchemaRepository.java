package com.smartuis.api.repository;

import com.smartuis.api.models.schema.Schema;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemaRepository extends ReactiveMongoRepository<Schema, String> {
}
