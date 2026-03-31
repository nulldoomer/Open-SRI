// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

public interface TimeoutStep {
  BuildStep timeout(int seconds);
}
