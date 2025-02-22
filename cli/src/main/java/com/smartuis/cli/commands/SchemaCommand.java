package com.smartuis.cli.commands;


import com.smartuis.cli.commands.schema.CreateSchemaCommand;
import com.smartuis.cli.commands.schema.DeleteSchemaCommand;
import com.smartuis.cli.commands.schema.ListSchemaCommand;
import com.smartuis.cli.commands.schema.TemplateSchemaCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;


@Component
@Command(
        name = "schema",
        version = "1.0.0",
        aliases = {"s"},
        mixinStandardHelpOptions = true,
        description = "Manage schemas.",
        subcommands = {
                CreateSchemaCommand.class,
                ListSchemaCommand.class,
                DeleteSchemaCommand.class,
                TemplateSchemaCommand.class
        }
)
public class SchemaCommand {
}
