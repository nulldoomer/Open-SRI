// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

/**
 * Defines the builder step that configures the network timeout of the client.
 *
 * <p>Once the timeout is set, the builder advances to the document version step
 * so the resulting client is fully configured for both transport and XML generation.
 */
public interface TimeoutStep {
  /**
   * Stores the timeout to be applied to SRI service calls.
   *
   * @param seconds timeout value in seconds
   * @return next step that requires the document version
   */
  DocumentVersionStep timeout(int seconds);
}
