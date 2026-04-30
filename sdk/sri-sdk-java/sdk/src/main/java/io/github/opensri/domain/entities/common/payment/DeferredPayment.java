// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.payment;

import io.github.opensri.domain.enums.PaymentMethod;
import io.github.opensri.domain.enums.TimeUnit;
import io.github.opensri.domain.valueobjects.Term;
import java.math.BigDecimal;

public record DeferredPayment(
    PaymentMethod paymentMethod, BigDecimal total, Term term, TimeUnit timeUnit)
    implements Payment {

  public DeferredPayment {

    if (paymentMethod == null) {
      throw new IllegalArgumentException("paymentMethod required");
    }

    if (total == null || total == BigDecimal.ZERO) {
      throw new IllegalArgumentException("Total has to be more than zero");
    }

    if (term == null || timeUnit == null) {
      throw new IllegalArgumentException("DeferredPayment requires" + "term and a time unit");
    }
  }
}
