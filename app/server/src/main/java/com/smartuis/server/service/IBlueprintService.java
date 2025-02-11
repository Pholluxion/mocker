package com.smartuis.server.service;

import com.smartuis.server.domain.blueprint.Blueprint;

import java.util.List;

public interface IBlueprintService {

    String create(Blueprint blueprint);

    boolean delete(String uuid);

    List<Blueprint> list();

    Blueprint setTemplate(String uuid, String template);

}
