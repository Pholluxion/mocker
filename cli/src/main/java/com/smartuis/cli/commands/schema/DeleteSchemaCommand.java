package com.smartuis.cli.commands.schema;


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
        name = "delete",
        aliases = {"d"},
        version = "1.0.0",
        mixinStandardHelpOptions = true,
        description = "Delete a schema."
)
public class DeleteSchemaCommand implements Callable<Integer> {

    @Value("${mocker.url}")
    private String baseUrl;

    @Option(names = {"-i", "--id"}, description = "Schema ID.", required = true)
    private String id;

    private final RestTemplate restTemplate;

    public DeleteSchemaCommand(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public Integer call() {

        var url = baseUrl + "/api/v1/schema/" + id;

        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.DELETE, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Schema deleted successfully.");
        } else {
            System.out.println("Failed to delete schema.");
        }

        return 0;

    }
}
