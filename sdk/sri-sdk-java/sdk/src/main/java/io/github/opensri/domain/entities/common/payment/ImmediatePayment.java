// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.payment;

import io.github.opensri.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public record ImmediatePayment(PaymentMethod paymentMethod, BigDecimal total) implements Payment {

  public ImmediatePayment {

    if (paymentMethod == null) {
      throw new IllegalArgumentException("paymentMethod required");
    }

    if (total == null || total == BigDecimal.ZERO) {
      throw new IllegalArgumentException("Total has to be more than zero");
    }
  }
}
