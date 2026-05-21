// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.util.Arrays;

/**
 * Representa el ambiente de operación de los servicios del SRI.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 4 de la documentación del SRI y permite distinguir
 * si el documento se genera para pruebas o para producción, afectando tanto la clave de acceso como
 * los endpoints utilizados por el SDK.
 */
public enum Environment {
  /** Ambiente de pruebas o certificación. */
  PRUEBAS(1, "Ambiente de pruebas"),
  /** Ambiente de producción o real. */
  PRODUCCION(2, "Ambiente de producción");

  /** Código numérico del ambiente asignado por el SRI. */
  private int code;

  /** Descripción funcional del ambiente. */
  private String description;

  /**
   * Obtiene el código oficial del ambiente según la tabla Nro. 4 del SRI.
   *
   * @return código numérico del ambiente
   */
  public int getCode() {
    return code;
  }

  /**
   * Obtiene la descripción funcional del ambiente.
   *
   * @return nombre legible del ambiente configurado
   */
  public String getDescription() {
    return description;
  }

  /**
   * Constructor del ambiente de operación.
   *
   * @param code código del ambiente
   * @param description descripción del ambiente
   */
  Environment(int code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Resuelve el ambiente correspondiente al código del SRI.
   *
   * @param code código del ambiente según la tabla Nro. 4 del SRI
   * @return ambiente correspondiente al código indicado
   * @throws OpenSRIValidationException si el código no existe en el catálogo
   */
  public static Environment fromCode(int code) {
    return Arrays.stream(values())
        .filter(v -> v.code == code)
        .findFirst()
        .orElseThrow(
            () ->
                new OpenSRIValidationException("No such EnvironmentEnum exists with code " + code));
  }
}
