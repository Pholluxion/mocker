package com.smartuis.cli;


import com.smartuis.cli.commands.MockerCommand;
import com.smartuis.cli.exceptions.MockerExceptionHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class CliApplication implements CommandLineRunner, ExitCodeGenerator {

    private final MockerCommand mockerCommand;
    private final IFactory factory;

    private int exitCode;

    public CliApplication(MockerCommand mockerCommand, IFactory factory) {
        this.mockerCommand = mockerCommand;
        this.factory = factory;
    }


    @Override
    public void run(String... args) {
        CommandLine cmd = new CommandLine(mockerCommand, factory);
        cmd.setExecutionExceptionHandler(new MockerExceptionHandler());
        exitCode = cmd.execute(args);

    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(CliApplication.class, args)));
    }


}
