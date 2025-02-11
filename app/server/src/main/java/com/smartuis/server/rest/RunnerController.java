package com.smartuis.server.rest;


import com.smartuis.server.domain.runner.Runner;
import com.smartuis.server.service.RunnerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("api/v1/runner")
public class RunnerController {


    private final RunnerService containerService;

    public RunnerController(RunnerService containerService) {
        this.containerService = containerService;
    }

    @GetMapping("/{uuid}")
    public void create(@PathVariable String uuid) {
        containerService.createContainer(uuid);
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable String uuid) {
        containerService.deleteContainer(uuid);
    }

    @GetMapping("/start/{uuid}")
    public void start(@PathVariable String uuid) {
        containerService.startContainer(uuid);
    }

    @GetMapping("/stop/{uuid}")
    public void stop(@PathVariable String uuid) {
        containerService.stopContainer(uuid);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Runner>> list() {
        return containerService.getContainers();
    }


}
