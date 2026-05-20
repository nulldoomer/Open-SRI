// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;

public class SRIAccessKeyGeneratorFactory {
  public static AccessKeyGenerator create() {
    return new SRIAccessKeyGenerator();
  }
}
