package com.smartuis.cli.exceptions;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

import java.io.IOException;

public class MockerExceptionHandler implements IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(Exception ex, CommandLine cmd, ParseResult parseResult) {
        switch (ex) {
            case HttpStatusCodeException httpEx -> {

                cmd.getErr().println("Error: " + httpEx.getResponseBodyAsString());
                return 1;
            }
            case IOException ioException -> {
               cmd.getErr().println(ioException.getMessage() );
                return 2;
            }
            case RestClientException restClientException -> {
                cmd.getErr().println(restClientException.getCause().getMessage());
                return 3;
            }
            case null, default -> {
                if (ex == null) throw new AssertionError();
                cmd.getErr().println("Unexpected error: " + ex.getMessage());
                return 99;
            }
        }
    }


}
