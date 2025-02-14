package com.smartuis.api.repository;

import com.smartuis.shared.schema.Schema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemaRepository extends MongoRepository<Schema, String> {
}
