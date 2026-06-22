// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.application.ports;

import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;

/**
 * Genera la clave de acceso del SRI para un documento tributario electrónico.
 *
 * <p>Este puerto define el contrato para producir la clave de acceso de 49 dígitos requerida por el
 * SRI para identificar un documento antes de su envío y controles de autorización. Las
 * implementaciones son responsables de combinar los metadatos del documento, el entorno y el dígito
 * verificador de acuerdo con las reglas del SRI.
 */
public interface AccessKeyGenerator {

  /**
   * Produce la clave de acceso para el contexto del documento proporcionado.
   *
   * <p>El valor generado debe ser adecuado para su inclusión en el XML del documento y para
   * posteriores consultas de autorización ante los servicios del SRI.
   *
   * @param date fecha de emisión del documento que se está identificando
   * @param documentNumber datos de numeración del documento, incluyendo código, establecimiento,
   *     punto de emisión y número secuencial
   * @param taxInfo información tributaria del emisor requerida por el formato de clave de acceso
   *     del SRI
   * @param environment entorno del SRI de destino para el cual se genera la clave
   * @return clave de acceso completa con dígito verificador
   */
  String generate(
      IssueDate date, DocumentNumber documentNumber, TaxInfo taxInfo, Environment environment);
}
