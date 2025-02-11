package com.smartuis.server.domain.interfaces;


public interface IRunner {

    /// getBlueprintName method
    String getBlueprintName();

    /// start method
    void start(Runnable onCompleteCallback);

    /// stop method
    void stop();


}
