package com.smartuis.cli.commands;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
        name = "mocker",
        version ="""
         _____         _
        |     |___ ___| |_ ___ ___
        | | | | . |  _| '_| -_|  _|
        |_|_|_|___|___|_,_|___|_| v0.1.0""" ,
        mixinStandardHelpOptions = true,
        description = "Mocker CLI tool",
        subcommands = {
                SimulationCommand.class,
                SchemaCommand.class,
        }
)
public class MockerCommand {
}
