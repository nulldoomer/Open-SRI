// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.valueobjects.IssueDate;

/**
 * Contrato común a todos los comprobantes electrónicos del SRI.
 *
 * <p>Expone los datos mínimos que el flujo de emisión necesita con independencia del tipo de
 * documento: la fecha de emisión, la numeración fiscal y la información tributaria del emisor son
 * suficientes para generar la clave de acceso, mientras que la versión del esquema permite a cada
 * serializador decidir qué secciones incluir.
 *
 * <p>Al implementar esta interfaz, cualquier documento (factura, nota de crédito, nota de débito,
 * liquidación de compra, guía de remisión o comprobante de retención) puede atravesar el mismo
 * pipeline genérico de generación de clave, serialización, firma y envío.
 */
public interface ElectronicDocument {

  /**
   * Devuelve la fecha de emisión del comprobante.
   *
   * @return fecha de emisión validada del documento
   */
  IssueDate issueDate();

  /**
   * Devuelve la numeración fiscal del comprobante.
   *
   * @return código de documento, establecimiento, punto de emisión y secuencial
   */
  DocumentNumber documentNumber();

  /**
   * Devuelve la información tributaria del emisor.
   *
   * @return datos tributarios reutilizables del emisor
   */
  TaxInfo taxInfo();

  /**
   * Devuelve la versión del esquema XML del comprobante.
   *
   * @return versión del documento a serializar
   */
  DocumentVersion documentVersion();
}
