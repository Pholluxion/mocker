package com.smartuis.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class Converter {

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    public Converter() {
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

        if (!exists(yamlFilePath)){
            System.err.println("File not found: " + yamlFilePath);
            System.exit(1);
        }

        JsonNode yamlTree = yamlMapper.readTree(new File(yamlFilePath));
        return jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(yamlTree);
    }

    /**
     * Reads a JSON file and returns its content as a pretty-printed JSON string.
     *
     * @param jsonFilePath Path to the JSON file.
     * @return String Pretty-printed JSON representation of the file content.
     * @throws IOException If an error occurs while reading the JSON file.
     */
    public String readJson(String jsonFilePath) throws IOException {
        if (!exists(jsonFilePath)){
            System.err.println("File not found: " + jsonFilePath);
            System.exit(1);
        }
        JsonNode jsonTree = jsonMapper.readTree(new File(jsonFilePath));
        return jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonTree);
    }


/**
         * Reads a text file and returns its content as a string.
         *
         * @param txtFilePath Path to the text file.
         * @return String Content of the text file.
         * @throws IOException If an error occurs while reading the text file.
         */
        public String readTxt(String txtFilePath) throws IOException {

            if (!exists(txtFilePath)){
                System.err.println("File not found: " + txtFilePath);
                System.exit(1);
            }

            Path path = Paths.get(txtFilePath);
            return Files.readString(path);
        }

        /**
         * Checks if a file exists at the given path.
         *
         * @param path Path to the file.
         * @return boolean True if the file exists, false otherwise.
         */
        public boolean exists(String path) {
            return Files.exists(Paths.get(path));
        }

}
