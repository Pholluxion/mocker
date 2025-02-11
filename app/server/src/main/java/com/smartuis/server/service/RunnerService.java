package com.smartuis.server.service;

import com.smartuis.server.domain.runner.Runner;
import com.smartuis.server.repository.BlueprintRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RunnerService {

    private final Map<String, Runner> containers = new HashMap<>();
    private final BlueprintRepository blueprintRepository;
    private static final String CONTAINER_NOT_FOUND = "Container not found";
    private static final String CONTAINER_ALREADY_EXISTS = "Container already exists";

    private final Sinks.Many<List<Runner>> sink = Sinks.many().replay().latest();

    public RunnerService(BlueprintRepository blueprintRepository) {
        this.blueprintRepository = blueprintRepository;
    }

    public void startContainer(String uuid) {
        Runner runner = containers.get(uuid);
        if (runner == null) {
            throw new IllegalArgumentException(CONTAINER_NOT_FOUND);
        }

        runner.start(this::emitUpdate);
        emitUpdate();
    }

    public void stopContainer(String uuid) {
        Runner runner = containers.get(uuid);
        if (runner == null) {
            throw new IllegalArgumentException(CONTAINER_NOT_FOUND);
        }

        runner.stop();
        emitUpdate();
    }

    public void createContainer(String blueprintUuid) {
        if (containers.containsKey(blueprintUuid)) {
            throw new IllegalArgumentException(CONTAINER_ALREADY_EXISTS);
        }

        Runner runner = new Runner(blueprintRepository.findById(blueprintUuid).orElseThrow());
        containers.put(blueprintUuid, runner);
        emitUpdate();
    }

    public void deleteContainer(String uuid) {
        Runner runner = containers.remove(uuid);
        if (runner == null) {
            throw new IllegalArgumentException(CONTAINER_NOT_FOUND);
        }

        runner.stop();
        emitUpdate();
    }

    public void stopAllContainers() {
        containers.values().forEach(Runner::stop);
        emitUpdate();
    }

    public void startAllContainers() {
        containers.values().forEach(
                runner -> runner.start(this::emitUpdate)
        );
        emitUpdate();
    }

    @PreDestroy
    public void deleteAllContainers() {
        containers.values().forEach(Runner::stop);
        containers.clear();
        emitUpdate();
    }

    @PostConstruct
    public void createAllContainers() {
        blueprintRepository.findAll().forEach(blueprint -> {
            if (!containers.containsKey(blueprint.getUuid())) {
                containers.put(blueprint.getUuid(), new Runner(blueprint));
            }
        });
        emitUpdate();
    }

    public void restartAllContainers() {
        stopAllContainers();
        startAllContainers();
    }

    public Flux<List<Runner>> getContainers() {
        return sink.asFlux();
    }

    private void emitUpdate() {
        sink.tryEmitNext(Collections.unmodifiableList(List.copyOf(containers.values())));
    }
}
