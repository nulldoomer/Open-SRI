// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.taxes;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Representa un impuesto aplicado sobre una base imponible dentro de un detalle.
 *
 * <p>Modela la combinación entre el tipo de impuesto, la tarifa utilizada y la base imponible sobre
 * la que debe calcularse el valor monetario del tributo conforme a las tablas del SRI.
 *
 * @param code código del tipo de impuesto aplicado
 * @param rateCode código de la tarifa o porcentaje de impuesto
 * @param rate porcentaje numérico aplicado para el cálculo
 * @param taxableBase base imponible sobre la que se aplica el impuesto
 */
public record Tax(
    // Tabla 16: Tipo de impuesto
    String code,
    // Tabla 17: Tarifa del IVA
    String rateCode,
    // Porcentaje aplicado del impuesto
    BigDecimal rate,
    // Base imponible a la que se le aplica el impuesto
    BigDecimal taxableBase) {

  private static final int SCALE = 2;

  /**
   * Calcula el valor monetario del impuesto a partir de su base imponible y tarifa.
   *
   * <p>El resultado se redondea a dos decimales con {@link RoundingMode#HALF_UP}, siguiendo el
   * formato monetario esperado por los comprobantes electrónicos.
   *
   * @return valor del impuesto calculado
   */
  public BigDecimal value() {
    return taxableBase
        .multiply(rate)
        .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP)
        .setScale(SCALE, RoundingMode.HALF_UP);
  }
}
