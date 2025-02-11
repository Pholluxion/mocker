package com.smartuis.server.repository;

import com.smartuis.server.domain.blueprint.Blueprint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlueprintRepository extends MongoRepository<Blueprint, String> {

    boolean existsByName(String name);

}
