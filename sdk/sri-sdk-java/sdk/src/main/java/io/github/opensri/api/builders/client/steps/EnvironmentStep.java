// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

import io.github.opensri.domain.enums.Environment;

public interface EnvironmentStep {
  CertificateStep environment(Environment environment);
}
