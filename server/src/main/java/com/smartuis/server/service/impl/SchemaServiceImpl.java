package com.smartuis.server.service.impl;

import com.smartuis.server.repository.SchemaRepository;
import com.smartuis.server.models.schema.Schema;
import com.smartuis.server.service.SchemaService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class SchemaServiceImpl implements SchemaService {

    private final SchemaRepository schemaRepository;

    public SchemaServiceImpl(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    @Override
    public Mono<Schema> create(Schema schema) {
        return schemaRepository.findByName(schema.getName())
                .flatMap(s -> Mono.error(new IllegalArgumentException("Schema with name " + schema.getName() + " already exists")))
                .then(schemaRepository.save(schema));
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
    public Mono<Schema> existsByName(String name) {
        return schemaRepository.findByName(name);

    }

    @Override
    public Mono<Schema> template(String id, Object template) {
        return schemaRepository.findById(id)
                .map(schema -> {
                    schema.setTemplate(template);
                    return schema;
                })
                .flatMap(schemaRepository::save);
    }


}
