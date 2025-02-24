package com.smartuis.cli.commands;

import com.smartuis.cli.commands.simulation.*;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;


@Component
@Command(
        name = "simulation",
        version = "1.0.0",
        aliases = {"sim"},
        mixinStandardHelpOptions = true,
        description = "Manage simulations.",
        subcommands = {
                CreateSimulationCommand.class,
                ListSimulationCommand.class,
                StartSimulationCommand.class,
                StopSimulationCommand.class,
                KillSimulationCommand.class
        }
)
public class SimulationCommand {
}
