package io.github.opensri.api.builders.client;

import io.github.opensri.api.builders.client.steps.EnvironmentStep;
import io.github.opensri.api.builders.client.steps.Steps;

public class OpenSRIClientBuilder {
    private OpenSRIClientBuilder() {}

    public static EnvironmentStep builder(){ return new Steps();}
}
