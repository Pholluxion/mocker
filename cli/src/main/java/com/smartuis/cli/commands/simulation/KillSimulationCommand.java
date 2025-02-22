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
        name = "kill",
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "Kill a simulation."
)
public class KillSimulationCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;
    private final RestTemplate restTemplate;

    @Option(names = {"-i", "--id"}, description = "Schema ID.", required = true)
    private String id;

    public KillSimulationCommand(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Integer call() throws Exception {
        ResponseEntity<String> response = restTemplate
                .exchange(baseUrl + "/api/v1/simulation/kill/" + id, HttpMethod.GET,null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Simulation killed successfully.");
        } else {
            System.out.println("Failed to kill simulation.");
        }

        return 0;
    }
}
