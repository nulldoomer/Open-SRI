// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.util.Arrays;

/**
 * Representa el tipo de identificación tributaria del comprador.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 6 de la documentación del SRI y define los códigos
 * válidos para la etiqueta {@code tipoIdentificacionComprador} en los comprobantes electrónicos.
 */
public enum IdentificationType {
  RUC("04", "RUC"),
  CEDULA("05", "CÉDULA"),
  PASAPORTE("06", "PASAPORTE"),
  VENTA_CONSUMIDOR_FINAL("07", "VENTA CONSUMIDOR FINAL"),
  IDENTIFICATION_DEL_EXTERIOR("08", "IDENTIFICATION DEL " + "EXTERIOR");

  private String code;
  private String description;

  /**
   * Obtiene el código oficial del tipo de identificación según la tabla Nro. 6 del SRI.
   *
   * @return código tributario del tipo de identificación
   */
  public String getCode() {
    return code;
  }

  /**
   * Obtiene la descripción legible del tipo de identificación.
   *
   * @return nombre funcional del tipo de identificación
   */
  public String getDescription() {
    return description;
  }

  IdentificationType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Resuelve el tipo de identificación correspondiente al código del SRI.
   *
   * @param code código del tipo de identificación según la tabla Nro. 6 del SRI
   * @return tipo de identificación asociado al código indicado
   * @throws OpenSRIValidationException si el código no existe en el catálogo
   */
  public static IdentificationType fromCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.code.equals(code))
        .findFirst()
        .orElseThrow(
            () -> new OpenSRIValidationException("No such IdentificationTypeEnum code " + code));
  }
}
