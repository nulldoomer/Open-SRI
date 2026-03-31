// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.InvoiceItem;

public interface ItemsStep {
  ItemsStep addItem(InvoiceItem item);

  FirstAdditionalInfoStep doneItems();
}
