// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.util.Arrays;

/**
 * Representa el medio de pago utilizado en la transacción.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 24 de la documentación del SRI. Aunque actualmente
 * no se encuentra integrada en el documento principal oficial, el SDK la conserva para mapear los
 * códigos tributarios de formas de pago aceptadas.
 */
public enum PaymentMethod {
  SIN_SISTEMA_FINANCIERO("01", "Sin utilización del sistema" + " financiero"),
  COMPENSACION_DE_DEUDAS("15", "Compensación de deudas"),
  TARJETA_DE_DEBITO("16", "Tarjeta de debito"),
  DINERO_ELECTRONICO("17", "Dinero Electrónico"),
  TARJETA_PREPAGO("18", "Tarjeta prepago"),
  TARJETA_DE_CREDITO("19", "Tarjeta de crédito"),
  OTROS_CON_SISTEMA_FINANCIERO("20", "Otros con utilización" + "del sistema financiero"),
  ENDOSO_DE_TITULOS("21", "Endoso de títulos");

  private String code;
  private String description;

  /**
   * Obtiene el código oficial del medio de pago según la tabla Nro. 24 del SRI.
   *
   * @return código tributario del método de pago
   */
  public String getCode() {
    return code;
  }

  /**
   * Obtiene la descripción legible del método de pago.
   *
   * @return nombre funcional de la forma de pago
   */
  public String getDescription() {
    return description;
  }

  PaymentMethod(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Resuelve el método de pago asociado al código del SRI.
   *
   * @param code código del método de pago según la tabla Nro. 24 del SRI
   * @return método de pago correspondiente
   * @throws OpenSRIValidationException si el código no existe en el catálogo
   */
  public static PaymentMethod fromCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.code.equals(code))
        .findFirst()
        .orElseThrow(
            () ->
                new OpenSRIValidationException(
                    "No such PaymentMethodEnum exists with code " + code));
  }
}
