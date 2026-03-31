// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.Client;

public interface ClientStep {
  FirstItemStep client(Client client);
}
