package com.smartuis.cli.commands.schema;

import com.smartuis.cli.utils.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Component
@Command(
        name = "create",
        aliases = {"c"},
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "Create a new schema."
)
public class CreateSchemaCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;
    private final RestTemplate restTemplate;
    private final Converter converter;

    @Option(names = {"-f", "--file"}, description = "Path to the YAML file.", required = true)
    private String path;

    public CreateSchemaCommand(RestTemplate restTemplate, Converter converter) {
        this.restTemplate = restTemplate;
        this.converter = converter;
    }

    @Override
    public Integer call() throws Exception {

        String json = converter.convertYamlToJson(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        var url = baseUrl + "/api/v1/schema";

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Schema created successfully.");
        } else {
            System.out.println("Failed to create schema.");
        }

        return 0;
    }
}
