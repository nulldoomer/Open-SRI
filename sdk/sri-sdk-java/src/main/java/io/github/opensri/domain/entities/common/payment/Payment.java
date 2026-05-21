// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.payment;

import io.github.opensri.domain.enums.PaymentMethod;
import java.math.BigDecimal;

/**
 * Sealed interface representing a payment entry in an electronic invoice.
 *
 * <p>Implementations cover the two payment modes accepted by the SRI: {@link ImmediatePayment} for
 * cash or same-day settlements, and {@link DeferredPayment} for credit transactions that include a
 * term and time unit.
 *
 * @see ImmediatePayment
 * @see DeferredPayment
 */
public sealed interface Payment permits ImmediatePayment, DeferredPayment {
  /**
   * Returns the payment method used for this payment.
   *
   * @return the payment method
   */
  PaymentMethod paymentMethod();

  /**
   * Returns the total amount for this payment.
   *
   * @return the total amount as BigDecimal
   */
  BigDecimal total();
}
