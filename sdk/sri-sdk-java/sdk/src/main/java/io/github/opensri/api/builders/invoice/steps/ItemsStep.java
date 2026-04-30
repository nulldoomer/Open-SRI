// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.Currency;
import java.util.List;

public interface ItemsStep {
  ItemsStep addItems(List<InvoiceItem> items);

  ItemsStep addCurrency(Currency currency);

  ItemsStep addInfos(List<AdditionalInfo> infos);

  PaymentStep addPayments(List<Payment> payments);
}
