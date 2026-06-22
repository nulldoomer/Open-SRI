// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.enums;

import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Representa las tarifas de IVA y estados relacionados admitidos por el SRI.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 17 de la documentación del SRI y define tanto el
 * código tributario como el porcentaje monetario asociado cuando la tarifa representa un valor
 * numérico aplicable.
 */
public enum TaxRate {
  /** Tarifa 0%. */
  PORCENTAJE_0(0, "0%", BigDecimal.ZERO),
  /** Tarifa 12%. */
  PORCENTAJE_12(2, "12%", BigDecimal.valueOf(12f)),
  /** Tarifa 14%. */
  PORCENTAJE_14(3, "14%", BigDecimal.valueOf(14f)),
  /** Tarifa 15%. */
  PORCENTAJE_15(4, "15%", BigDecimal.valueOf(15f)),
  /** Tarifa 5%. */
  PORCENTAJE_5(5, "5%", BigDecimal.valueOf(5f)),
  /** No objeto de impuesto. */
  NO_OBJETO_IMPUESTO(6, "No objeto de Impuesto", null),
  /** Exento de IVA. */
  EXENTO_IVA(7, "Exento de IVA", null),
  /** IVA diferenciado. */
  IVA_DIFERENCIADO(8, "IVA diferenciado", null),
  /** Tarifa 13%. */
  PORCENTAJE_13(10, "13%", BigDecimal.valueOf(13f));

  private final int code;
  private final String description;
  private final BigDecimal value;

  /**
   * Obtiene el código oficial de la tarifa según la tabla Nro. 17 del SRI.
   *
   * @return código tributario de la tarifa
   */
  public int getCode() {
    return code;
  }

  /**
   * Obtiene la descripción legible de la tarifa o condición tributaria.
   *
   * @return descripción funcional de la tarifa
   */
  public String getDescription() {
    return description;
  }

  /**
   * Obtiene el porcentaje numérico asociado a la tarifa cuando aplica.
   *
   * @return porcentaje monetario de la tarifa, o {@code null} si la categoría no representa un
   *     valor porcentual directo
   */
  public BigDecimal getValue() {
    return value;
  }

  TaxRate(int code, String description, BigDecimal value) {
    this.code = code;
    this.description = description;
    this.value = value;
  }

  /**
   * Resuelve la tarifa de IVA correspondiente al código del SRI.
   *
   * @param code código de tarifa según la tabla Nro. 17 del SRI
   * @return tarifa correspondiente al código indicado
   * @throws OpenSRIValidationException si el código no existe en el catálogo
   */
  public static TaxRate fromCode(int code) {
    return Arrays.stream(values())
        .filter(v -> v.code == code)
        .findFirst()
        .orElseThrow(() -> new OpenSRIValidationException("No such TaxRateEnum code " + code));
  }
}
