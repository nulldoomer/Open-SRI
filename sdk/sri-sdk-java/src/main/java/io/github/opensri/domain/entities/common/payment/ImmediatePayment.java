// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.payment;

import io.github.opensri.domain.enums.PaymentMethod;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.math.BigDecimal;

/**
 * Representa un pago inmediato o de contado.
 *
 * <p>Modela el método de pago y el monto total para transacciones que se liquidan al momento de la
 * emisión del comprobante.
 *
 * @param paymentMethod método de pago utilizado
 * @param total monto total del pago
 */
public record ImmediatePayment(PaymentMethod paymentMethod, BigDecimal total) implements Payment {

  /**
   * Validates that the payment method is provided and the total is greater than zero.
   *
   * @throws OpenSRIValidationException if {@code paymentMethod} is null or {@code total} is null or
   *     zero
   */
  public ImmediatePayment {

    if (paymentMethod == null) {
      throw new OpenSRIValidationException("paymentMethod required");
    }

    if (total == null || total == BigDecimal.ZERO) {
      throw new OpenSRIValidationException("Total has to be more than zero");
    }
  }
}
