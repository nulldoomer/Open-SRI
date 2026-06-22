// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.enums;

import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;
import java.util.Arrays;

/**
 * Representa el tipo de comprobante electrónico emitido por el SDK.
 *
 * <p>Este catálogo corresponde a la tabla Nro. 3 de la documentación del SRI y define el código
 * oficial que debe incluirse en la numeración y en la clave de acceso del documento.
 */
public enum DocumentType {
  /** Comprobante de venta tipo factura. */
  FACTURA("01", "Factura"),
  /** Liquidación de compra de bienes y prestación de servicios. */
  LIQUIDACION_COMPRA("03", "Liquidación de Compra de Bienes" + " y Prestación de Servicios"),
  /** Comprobante para anular o modificar facturas. */
  NOTA_CREDITO("04", "Nota de Crédito"),
  /** Comprobante para sustentar costos o gastos adicionales. */
  NOTA_DEBITO("05", "Nota de Débito"),
  /** Documento que sustenta el traslado de mercaderías. */
  GUIA_REMISION("06", "Guía de Remisión"),
  /** Comprobante de retención de impuestos. */
  COMPROBANTE_RETENCION("07", "Comprobante de Retención");

  /** Código tributario asignado por el SRI. */
  private String code;

  /** Descripción legible del tipo de documento. */
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

  /**
   * Constructor del tipo de documento.
   *
   * @param code código tributario del SRI
   * @param description nombre funcional del documento
   */
  DocumentType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Resuelve el tipo de documento asociado al código del SRI.
   *
   * @param code código del comprobante según la tabla Nro. 3 del SRI
   * @return tipo de documento correspondiente
   * @throws OpenSRIValidationException si el código no existe en el catálogo
   */
  public static DocumentType fromCode(String code) {
    return Arrays.stream(values())
        .filter(v -> v.code.equals(code))
        .findFirst()
        .orElseThrow(() -> new OpenSRIValidationException("No such DocumentTypeEnum code " + code));
  }
}
