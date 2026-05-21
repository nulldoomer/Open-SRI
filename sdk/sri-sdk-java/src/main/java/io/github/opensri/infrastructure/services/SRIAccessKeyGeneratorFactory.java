// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;

/** Factory for creating AccessKeyGenerator instances used by the SDK. */
public class SRIAccessKeyGeneratorFactory {
  /** Private constructor to prevent instantiation. */
  private SRIAccessKeyGeneratorFactory() {}

  /**
   * Creates a new AccessKeyGenerator implementation.
   *
   * @return a new AccessKeyGenerator instance
   */
  public static AccessKeyGenerator create() {
    return new SRIAccessKeyGenerator();
  }
}
