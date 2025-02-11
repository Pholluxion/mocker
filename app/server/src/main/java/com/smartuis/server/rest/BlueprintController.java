package com.smartuis.server.rest;

import com.smartuis.server.domain.blueprint.Blueprint;
import com.smartuis.server.domain.blueprint.BlueprintWrapper;
import com.smartuis.server.service.BlueprintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/blueprint")
public class BlueprintController {

    private final BlueprintService blueprintService;

    public BlueprintController(BlueprintService blueprintService) {
        this.blueprintService = blueprintService;
    }


    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid BlueprintWrapper blueprintWrapper) {
        try {

            var blueprint = blueprintWrapper.getSimulation();

            var uuid = blueprintService.create(blueprint);

            var uri = URI.create("/api/v1/blueprint/" + uuid);

            return ResponseEntity.created(uri).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Blueprint>> readAll() {
        return ResponseEntity.ok(blueprintService.list());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> delete(@PathVariable String uuid) {

        var exist = blueprintService.delete(uuid);

        if (!exist) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<String> setTemplate(@PathVariable String uuid, @RequestBody String template) {
        try {
            var blueprint = blueprintService.setTemplate(uuid, template);
            return ResponseEntity.ok().body(blueprint.getTemplate());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
