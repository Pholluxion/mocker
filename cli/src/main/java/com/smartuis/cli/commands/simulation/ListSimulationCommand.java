package com.smartuis.cli.commands.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
        name = "list-all",
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "List all simulations."
)
public class ListSimulationCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public ListSimulationCommand(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Integer call() throws Exception {

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/v1/simulation", HttpMethod.GET, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("-".repeat(25) + "---" + "-".repeat(30) + "---" + "-".repeat(10));
            System.out.printf("%-25s | %-30s | %-10s%n", "ID", "Name","State");
            System.out.println("-".repeat(25) + "---" + "-".repeat(30) + "---" + "-".repeat(10));

            objectMapper.reader()
                    .readTree(response.getBody())
                    .forEach(schema -> printSchema(schema.get("id").asText(), schema.get("name").asText(), schema.get("state").asText()));

            System.out.println("-".repeat(25) + "---" + "-".repeat(30) + "---" + "-".repeat(10));
        } else {
            System.out.println("Failed to list schemas.");
        }

        return 0;
    }

    private void printSchema(String id, String name, String status) {
        System.out.printf("%-25s | %-30s | %-10s%n", id, name, status);
    }
}
