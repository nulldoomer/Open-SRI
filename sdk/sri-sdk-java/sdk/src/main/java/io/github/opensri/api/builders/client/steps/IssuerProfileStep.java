// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

import io.github.opensri.domain.entities.common.IssuerProfile;

public interface IssuerProfileStep {
  TimeoutStep issuerProfile(IssuerProfile issuerProfile);
}
