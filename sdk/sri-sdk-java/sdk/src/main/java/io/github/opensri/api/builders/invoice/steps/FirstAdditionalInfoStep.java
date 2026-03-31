// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.Invoice;

public interface FirstAdditionalInfoStep {
  AdditionalInfoStep addInfo(AdditionalInfo info);

  Invoice build();
}
