package com.smartuis.cli.commands;

import com.smartuis.cli.utils.YamlToJsonConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@ShellComponent
public class MockerCommand {

    @Value("${mocker.url}")
    private String baseUrl;
    private final RestTemplate restTemplate;
    private final YamlToJsonConverter converter;

    public MockerCommand(RestTemplate restTemplate, YamlToJsonConverter converter) {
        this.restTemplate = restTemplate;
        this.converter = converter;
    }

    @ShellMethod(key = "create", value = "Create a new schema.")
    public String create(@ShellOption(value = "path") String path) {
        try {
            var json = converter.convertYamlToJson(path);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            return restTemplate.exchange(baseUrl + "/api/v1/schema", HttpMethod.POST, entity, String.class).getBody();

        } catch (HttpStatusCodeException e) {
            return String
                    .format("Error: %d %s%nURL: %s%nResponse: %s",
                            e.getStatusCode().value(),
                            e.getStatusText(), baseUrl + "/api/v1/schema",
                            e.getResponseBodyAsString()
                    );
        } catch (IOException e) {
            return "Failed to read or convert the file: " + e.getMessage();
        } catch (RestClientException e) {
            return "Request failed: " + e.getMessage();
        } catch (Exception e) {
            return "An unexpected error occurred: " + e.getMessage();
        }
    }

}
