// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.common.payment;

import io.github.nulldoomer.opensri.domain.enums.PaymentMethod;
import io.github.nulldoomer.opensri.domain.enums.TimeUnit;
import io.github.nulldoomer.opensri.domain.valueobjects.Term;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;
import java.math.BigDecimal;

/**
 * Representa un pago con condiciones de diferimiento.
 *
 * <p>Modela el método de pago, el monto total, el plazo y la unidad de tiempo asociados a una forma
 * de pago que no es inmediata, conforme a los requerimientos del SRI.
 *
 * @param paymentMethod método de pago utilizado
 * @param total monto total del pago
 * @param term plazo del pago
 * @param timeUnit unidad de tiempo del plazo
 */
public record DeferredPayment(
    PaymentMethod paymentMethod, BigDecimal total, Term term, TimeUnit timeUnit)
    implements Payment {

  /**
   * Valida los campos de un pago diferido.
   *
   * <p>Lanza {@link OpenSRIValidationException} si falta el método de pago, el total es cero o
   * nulo, o si el plazo o la unidad de tiempo no están presentes.
   */
  public DeferredPayment {

    if (paymentMethod == null) {
      throw new OpenSRIValidationException("paymentMethod required");
    }

    if (total == null || total.equals(BigDecimal.ZERO)) {
      throw new OpenSRIValidationException("Total has to be more than zero");
    }

    if (term == null || timeUnit == null) {
      throw new OpenSRIValidationException("DeferredPayment requires" + "term and a time unit");
    }
  }
}
