package com.smartuis.server.domain.runner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.samskivert.mustache.Mustache;
import com.smartuis.server.domain.blueprint.Blueprint;
import com.smartuis.server.domain.interfaces.IGenerator;
import com.smartuis.server.domain.interfaces.IRunner;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import reactor.core.Disposable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Runner implements IRunner {

    @Id
    private final String uuid;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @JsonIgnore
    private final Blueprint blueprint;

    @JsonIgnore
    private Disposable disposable;

    public Runner(Blueprint blueprint) {
        this.blueprint = blueprint;
        this.uuid = blueprint.getUuid();
    }

    @Override
    public String getBlueprintName() {
        return blueprint.getName();
    }

    @Override
    public void start(Runnable onCompleteCallback) {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        disposable = blueprint.getSampler().sample()
                .doOnNext(interval -> generateData())
                .takeWhile(interval -> isRunning.get())
                .doOnComplete(() -> {
                    isRunning.set(false);
                    Optional.ofNullable(onCompleteCallback).ifPresent(Runnable::run);
                })
                .subscribe();

        log.info("Simulation started");
    }

    @Override
    @PreDestroy
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            log.info("Simulation stopped");
        }
    }

    public void generateData() {
        Map<String, Object> map = blueprint.getGenerators().stream()
                .collect(Collectors.toMap(IGenerator::name, IGenerator::value));

        String template = Optional.ofNullable(blueprint.getTemplate()).orElse("");

        log.info(template.isEmpty() ? map.toString() : Mustache.compiler().compile(template).execute(map));
    }
}
