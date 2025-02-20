package com.smartuis.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class YamlToJsonConverter {

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    public YamlToJsonConverter() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.jsonMapper = new ObjectMapper();
    }

    /**
     * Convert a YAML file to JSON.
     *
     * @param yamlFilePath Path to the YAML file.
     * @return String JSON representation of the YAML file.
     * @throws IOException If an error occurs while reading the YAML file.
     */
    public String convertYamlToJson(String yamlFilePath) throws IOException {
        JsonNode yamlTree = yamlMapper.readTree(new File(yamlFilePath));
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(yamlTree);
    }
}
