package com.smartuis.cli.commands.schema;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Component
@Command(
        name = "list-all",
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "List all schemas."
)
public class ListSchemaCommand implements Callable<Integer> {
    @Value("${mocker.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public ListSchemaCommand(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public Integer call() throws JsonProcessingException {



        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/v1/schema/short", HttpMethod.GET, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
             System.out.println("-".repeat(25) + "---" + "-".repeat(30));
             System.out.printf("%-25s | %-30s%n", "ID", "Name");
             System.out.println("-".repeat(25) + "---" + "-".repeat(30));

            objectMapper.reader()
                    .readTree(response.getBody())
                    .forEach(schema -> printSchema(schema.get("id").asText(), schema.get("name").asText()));

             System.out.println("-".repeat(25) + "---" + "-".repeat(30));
        } else {
             System.out.println("Failed to list schemas.");
        }

        return 0;
    }


    private void printSchema(String id,String name) {
             System.out.printf("%-25s | %-30s%n", id, name);
    }
}
