// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

import java.util.Arrays;

/**
 * Representa el tipo de comprobante electrónico emitido por el SDK.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 3 de la documentación del SRI y define el código
 * oficial que debe incluirse en la numeración y en la clave de acceso del documento.
 */
public enum DocumentType {
  FACTURA("01", "Factura"),
  LIQUIDACION_COMPRA("03", "Liquidación de Compra de Bienes" + " y Prestación de Servicios"),
  NOTA_CREDITO("04", "Nota de Crédito"),
  NOTA_DEBITO("05", "Nota de Débito"),
  GUIA_REMISION("06", "Guía de Remisión"),
  COMPROBANTE_RETENCION("07", "Comprobante de Retención");

  private String code;
  private String description;

  /**
   * Obtiene el código oficial del tipo de documento según la tabla Nro. 3 del SRI.
   *
   * @return código tributario del tipo de comprobante
   */
  public String getCode() {
    return code;
  }

  /**
   * Obtiene la descripción legible del tipo de comprobante.
   *
   * @return nombre funcional del documento
   */
  public String getDescription() {
    return description;
  }

  DocumentType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Resuelve el tipo de documento asociado al código del SRI.
   *
   * @param code código del comprobante según la tabla Nro. 3 del SRI
   * @return tipo de documento correspondiente
   * @throws IllegalArgumentException si el código no existe en el catálogo
   */
  public static DocumentType fromCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.code.equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such DocumentTypeEnum code " + code));
  }
}
