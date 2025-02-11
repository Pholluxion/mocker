package com.smartuis.server.service;

import com.smartuis.server.domain.blueprint.Blueprint;
import com.smartuis.server.repository.BlueprintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlueprintService implements IBlueprintService {

    private final BlueprintRepository repository;


    public BlueprintService(BlueprintRepository repository) {
        this.repository = repository;
    }


    @Override
    public String create(Blueprint blueprint) {

        if (repository.existsByName(blueprint.getName())) {
            throw new IllegalArgumentException("Blueprint with name " + blueprint.getName() + " already exists");
        }

        return repository.insert(blueprint).getUuid();
    }

    @Override
    public boolean delete(String uuid) {
        repository.deleteById(uuid);
        return repository.existsById(uuid);
    }


    @Override
    public List<Blueprint> list() {
        return repository.findAll();
    }

    @Override
    public Blueprint setTemplate(String uuid, String template) {
        var blueprint = repository.findById(uuid);

        if (blueprint.isEmpty()) {
            throw new IllegalArgumentException("Blueprint with uuid " + uuid + " does not exist");
        }

        var blueprintToUpdate = blueprint.get();

        blueprintToUpdate.setTemplate(template);

        return repository.save(blueprint.get());


    }
}
