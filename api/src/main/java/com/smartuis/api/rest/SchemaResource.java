package com.smartuis.api.rest;

import com.smartuis.api.repository.SchemaRepository;
import com.smartuis.api.service.SchemaService;
import com.smartuis.shared.schema.Schema;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/schema")
public class SchemaResource {

    private final SchemaService schemaService;

    public SchemaResource(SchemaService schemaService) {
        this.schemaService = schemaService;
    }


    @PostMapping
    public Mono<ResponseEntity<Schema>> createSchema(@RequestBody @Valid Schema schema) {

        var savedSchema = schemaService.createSchema(schema);

        return Mono.just(ResponseEntity.ok(savedSchema));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Schema> getSchema(@PathVariable String id) {
        var schema = schemaService.getSchema(id);
        return schema != null ? ResponseEntity.ok(schema) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public Mono<ResponseEntity<Iterable<Schema>>> getSchemas() {
        var schemas = schemaService.getSchemas();
        return Mono.just(ResponseEntity.ok(schemas));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchema(@PathVariable String id) {
        schemaService.deleteSchema(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/template")
    public ResponseEntity<Schema> setTemplate(@PathVariable String id, @RequestBody String template) {
        var schema = schemaService.setTemplate(id, template);
        return schema != null ? ResponseEntity.ok(schema) : ResponseEntity.notFound().build();
    }



}
