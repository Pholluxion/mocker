package com.smartuis.api.rest;

import com.smartuis.api.service.SchemaService;
import com.smartuis.api.models.schema.Schema;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/schema")
public class SchemaResource {

    private final SchemaService schemaService;

    public SchemaResource(SchemaService schemaService) {
        this.schemaService = schemaService;
    }


    @PostMapping
    public Mono<ResponseEntity<Schema>> create(@RequestBody @Valid Schema schema) {

        var name = schema.getName();

        return schemaService.existsByName(name)
                .flatMap(exists -> exists ? Mono.empty() : Mono.just(schema))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Schema with name " + name + " already exists")))
                .flatMap(schemaService::create)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());

    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Schema>> getById(@PathVariable String id) {
        return schemaService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Schema>>> list() {
        return Mono.just(ResponseEntity.ok(schemaService.findAll()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return schemaService.delete(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @PutMapping("/{id}/template")
    public Mono<ResponseEntity<Schema>> template(@PathVariable String id, @RequestBody Map<String, Object> template) {
        return schemaService.template(id, template)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


}
