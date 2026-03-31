// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

import java.util.Arrays;

/**
 * Representa el ambiente de operación de los servicios del SRI.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 4 de la documentación del SRI y permite distinguir
 * si el documento se genera para pruebas o para producción, afectando tanto la clave de acceso como
 * los endpoints utilizados por el SDK.
 */
public enum Environment {
  PRUEBAS(1, "Ambiente de pruebas"),
  PRODUCCION(2, "Ambiente de producción");

  private int code;
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

  Environment(int code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Resuelve el ambiente correspondiente al código del SRI.
   *
   * @param code código del ambiente según la tabla Nro. 4 del SRI
   * @return ambiente correspondiente al código indicado
   * @throws IllegalArgumentException si el código no existe en el catálogo
   */
  public static Environment fromCode(int code) {
    return Arrays.stream(values())
        .filter(v -> v.code == code)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("No such EnvironmentEnum exists with code " + code));
  }
}
