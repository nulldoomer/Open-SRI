// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import java.util.List;

public interface ItemsStep extends BuildStep {
  ItemsStep addItems(List<InvoiceItem> items);

  ItemsStep addInfos(List<AdditionalInfo> infos);
}
