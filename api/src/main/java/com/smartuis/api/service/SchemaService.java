package com.smartuis.api.service;

import com.smartuis.api.repository.SchemaRepository;
import com.smartuis.shared.schema.Schema;
import org.springframework.stereotype.Service;

@Service
public class SchemaService {

    private final SchemaRepository schemaRepository;

    public SchemaService(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }


    public Schema createSchema(Schema schema) {
        return schemaRepository.save(schema);
    }

    public Schema getSchema(String id) {
        var schema = schemaRepository.findById(id);
        return schema.orElse(null);

    }

    public Iterable<Schema> getSchemas() {
        return schemaRepository.findAll();
    }

    public void deleteSchema(String id) {
        schemaRepository.deleteById(id);
    }

    public Schema setTemplate(String id, String template) {
        var schema = schemaRepository.findById(id);
        if (schema.isEmpty()) {
            return null;
        }
        schema.get().schema().setTemplate(template);
        return schemaRepository.save(schema.get());
    }
}
