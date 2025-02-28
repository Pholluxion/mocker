package com.smartuis.server.rest;

import com.smartuis.server.dtos.SchemaDTO;
import com.smartuis.server.service.SchemaService;
import com.smartuis.server.models.schema.Schema;

import jakarta.validation.Valid;

import org.reactivestreams.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * SchemaResource is a REST controller that provides endpoints for managing schemas.
 */
@RestController
@RequestMapping("/api/v1/schema")
public class SchemaResource {

    private final SchemaService schemaService;

    /**
     * Constructs a SchemaResource with the given SchemaService.
     *
     * @param schemaService the service to handle schema operations
     */
    public SchemaResource(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    /**
     * Creates a new schema.
     *
     * @param schema the schema to create
     * @return a Mono containing the ResponseEntity with the created Schema or a bad request status
     */
    @PostMapping
    public Mono<ResponseEntity<Schema>> create(@RequestBody @Valid Schema schema) {
        return schemaService.create(schema)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    /**
     * Retrieves a schema by its ID.
     *
     * @param id the ID of the schema to retrieve
     * @return a Mono containing the ResponseEntity with the Schema or a not found status
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Schema>> getById(@PathVariable String id) {
        return schemaService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all schemas.
     *
     * @return a Mono containing the ResponseEntity with a Flux of Schemas
     */
    @GetMapping
    public Mono<ResponseEntity<Flux<Schema>>> list() {
        return Mono.just(ResponseEntity.ok(schemaService.findAll()));
    }


    /**
     * Retrieves all schemas.
     *
     * @return a Mono containing the ResponseEntity with a Flux of SchemasDTOs
     */
    @GetMapping("/short")
    public Mono<ResponseEntity<Flux<SchemaDTO>>> listShort() {
        return Mono.just(ResponseEntity.ok(schemaService.findAll().flatMap(
                schema -> schemaService.existsSimulation(schema.getId())
                        .defaultIfEmpty(false)
                        .map(created -> new SchemaDTO(schema.getId(), schema.getName(), created))
        )));
    }

   /**
 * Streams a list of SchemaDTOs at a specified interval.
 *
 * @param interval the interval in seconds at which to stream the list of SchemaDTOs
 * @return a Flux containing the list of SchemaDTOs
 */
@GetMapping(value = "/short/stream", produces = "text/event-stream")
public Flux<List<SchemaDTO>> listShortStream(@RequestParam int interval) {

    if (interval < 1 ) {
        interval = 1;
    }

    return Flux.interval(Duration.ofSeconds(interval))
            .flatMap(i -> schemaService.findAll().flatMap(
                    schema -> schemaService.existsSimulation(schema.getId())
                            .defaultIfEmpty(false)
                            .map(created -> new SchemaDTO(schema.getId(), schema.getName(), created))
            ).collectList());

}

    /**
     * Deletes a schema by its ID.
     *
     * @param id the ID of the schema to delete
     * @return a Mono containing the ResponseEntity with no content status
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return schemaService.delete(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    /**
     * Updates the template of a schema by its ID.
     *
     * @param id       the ID of the schema to update
     * @param template the new template data
     * @return a Mono containing the ResponseEntity with the updated Schema or a not found status
     */
    @PutMapping("/template/{id}")
    public Mono<ResponseEntity<Schema>> template(@PathVariable String id, @RequestBody Map<String, Object> template) {
        return schemaService.template(id, template)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}