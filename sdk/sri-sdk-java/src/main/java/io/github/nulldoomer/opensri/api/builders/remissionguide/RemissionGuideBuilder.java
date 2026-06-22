// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.remissionguide;

import io.github.nulldoomer.opensri.api.builders.remissionguide.steps.IssueDateStep;
import io.github.nulldoomer.opensri.api.builders.remissionguide.steps.Steps;
import io.github.nulldoomer.opensri.domain.entities.remissionguide.RemissionGuide;

/**
 * Exposes the entry point for the step-based construction of {@link RemissionGuide} instances.
 *
 * <p>Starts a fluent workflow that collects the mandatory remission-guide data in a fixed order and
 * requires at least one recipient before producing the final immutable entity.
 *
 * @see Steps
 */
public final class RemissionGuideBuilder {

  private RemissionGuideBuilder() {}

  /**
   * Starts the fluent builder flow for creating a remission guide.
   *
   * @return first step that requires the issue date
   */
  public static IssueDateStep builder() {
    return new Steps();
  }
}
