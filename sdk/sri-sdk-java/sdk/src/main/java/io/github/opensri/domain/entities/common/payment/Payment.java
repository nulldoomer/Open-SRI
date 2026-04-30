// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.payment;

import io.github.opensri.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public sealed interface Payment permits ImmediatePayment, DeferredPayment {

  PaymentMethod paymentMethod();

  BigDecimal total();
}
