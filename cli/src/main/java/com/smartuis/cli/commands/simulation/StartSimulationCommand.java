package com.smartuis.cli.commands.simulation;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;


@Component
@Command(
        name = "start",
        aliases = {"run"},
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "Start a simulation."
)
public class StartSimulationCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;
    private final RestTemplate restTemplate;

    @Option(names = {"-i", "--id"}, description = "Schema ID.", required = true)
    private String id;

    public StartSimulationCommand(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Integer call() throws Exception {

        var url = baseUrl + "/api/v1/simulation/start/" + id;

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.GET, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Simulation started successfully.");
        } else {
            System.out.println("Failed to start simulation.");
        }

        return 0;
    }
}
