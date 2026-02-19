package io.github.opensri.api.builders.client.steps;

import io.github.opensri.api.builders.client.steps.BuildStep;

public interface TimeoutStep {
    BuildStep timeout(int seconds);
}
