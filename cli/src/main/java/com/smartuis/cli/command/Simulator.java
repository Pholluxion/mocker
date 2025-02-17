package com.smartuis.cli.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@ShellComponent
public class Simulator {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8090/api/v1/container";

    @ShellMethod(key = "start", value = "Start the simulation.")
    public String startSimulation(String id ) {
        return restTemplate.getForObject(baseUrl + "/event/" + id + "?event=START", String.class);
    }

    @ShellMethod(key = "create", value = "Create a new container.")
    public String createContainer(String schemaId) {
        return restTemplate.getForObject(baseUrl + "/create?schemaId=" + schemaId, String.class);
    }
    @ShellMethod(key = "state", value = "Get the current state of the simulation.")
    public String getState(String id) {
        return restTemplate.getForObject(baseUrl + "/state/" + id, String.class);
    }

    @ShellMethod(key = "stop", value = "Stop the simulation.")
    public String stopSimulation(String id) {
        return restTemplate.getForObject(baseUrl + "/event/" + id + "?event=STOP", String.class);
    }

    @ShellMethod(key = "kill", value = "Kill the simulation.")
    public String killSimulation(String id) {
        return restTemplate.getForObject(baseUrl + "/event/" + id + "?event=KILL", String.class);
    }

    @ShellMethod(key = "list", value = "List all containers.")
    public String listContainers() {
        var list = restTemplate.getForObject(baseUrl, List.class);

        if (Objects.isNull(list) || list.isEmpty()) {
            return "No containers found.";
        }

        return list.toString();
    }

}
