// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.client;

import io.github.nulldoomer.opensri.api.builders.client.steps.EnvironmentStep;
import io.github.nulldoomer.opensri.api.builders.client.steps.Steps;

/**
 * Exposes the entry point for the step-based construction of an {@code OpenSRIClient}.
 *
 * <p>This builder hides the mutable implementation used during configuration and returns only the
 * first step of the fluent API. The resulting flow guides SDK consumers through the mandatory
 * client settings in the correct order before a client instance can be built.
 *
 * @see Steps
 */
public class OpenSRIClientBuilder {
  private OpenSRIClientBuilder() {}

  /**
   * Starts the fluent builder flow for creating an {@code OpenSRIClient}.
   *
   * @return first step that requires the target SRI environment
   */
  public static EnvironmentStep builder() {
    return new Steps();
  }
}
