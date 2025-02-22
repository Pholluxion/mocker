package com.smartuis.cli.commands;

import com.smartuis.cli.utils.YamlToJsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Slf4j
@Component
@Command(
        name = "create",
        mixinStandardHelpOptions = true,
        description = "Create a new schema."
)
public class MockerCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final YamlToJsonConverter converter;

    @Option(names = {"-f", "--file"}, description = "Path to the YAML file.", required = true)
    private String path;

    public MockerCommand(RestTemplate restTemplate, YamlToJsonConverter converter) {
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

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/v1/schema", HttpMethod.POST, entity, String.class);

        log.info("Schema created successfully: {}", response.getBody());

        return 0;

    }
}
