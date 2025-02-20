package com.smartuis.cli;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan(basePackages = "com.smartuis.cli.commands")
@SpringBootApplication
public class CliApplication {

    public static void main(String[] args) {

        SpringApplication.run(CliApplication.class, args);
    }



}
