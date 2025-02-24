package com.smartuis.cli.commands.schema;

import com.smartuis.cli.utils.YamlToJsonConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Component
@Command(
        name = "template",
        aliases = {"t"},
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "Set the schema template."
)
public class TemplateSchemaCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;
    private final RestTemplate restTemplate;
    private final YamlToJsonConverter converter;

    @Option(names = {"-f", "--file"}, description = "Path to the template file.", required = true)
    private String path;

    @Option(names = {"-i", "--id"}, description = "Schema ID.", required = true)
    private String id;

    public TemplateSchemaCommand(RestTemplate restTemplate, YamlToJsonConverter converter) {
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

        var url = baseUrl + "/api/v1/schema/template/" + id;

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Schema template created successfully.");
        } else {
            System.out.println("Error creating schema template.");
        }

        return 0;
    }
}
