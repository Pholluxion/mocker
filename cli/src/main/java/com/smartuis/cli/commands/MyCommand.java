package com.smartuis.cli.commands;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "mycommand", mixinStandardHelpOptions = true, subcommands = MyCommand.Sub.class)
public class MyCommand implements Callable<Integer> {
    private String x;

    @Option(names = "-x", description = "optional option")
    public void setX(String x) {
        this.x = x;
    }

    private List<String> positionals;

    @Parameters(description = "positional params")
    public void setPositionals(List<String> positionals) {
        this.positionals = positionals;
    }

    @Override
    public Integer call() {
        System.out.printf("mycommand was called with -x=%s and positionals: %s%n", x, positionals);
        return 23;
    }

    @Component
    @Command(name = "sub", mixinStandardHelpOptions = true, subcommands = MyCommand.SubSub.class,
            exitCodeOnExecutionException = 34)
    static class Sub implements Callable<Integer> {
        @Option(names = "-y", description = "optional option")
        private String y;

        @Parameters(description = "positional params")
        private List<String> positionals;

        @Override
        public Integer call() {
            System.out.printf("mycommand sub was called with -y=%s and positionals: %s%n", y, positionals);
            return 33;
        }
    }

    @Component
    @Command(name = "subsub", mixinStandardHelpOptions = true,
            exitCodeOnExecutionException = 44)
    static class SubSub implements Callable<Integer> {
        @Option(names = "-z", description = "optional option")
        private String z;

        @Override
        public Integer call() {
            System.out.printf("mycommand sub subsub was called with");
            return 43;
        }
    }
}