// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models.common;

import io.github.opensri.domain.entities.common.Compensation;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a {@code compensacion} entry in credit and debit notes.
 *
 * <p>The compensation code is fixed to {@code "1"} per the SRI tables, so only the rate and value
 * are mapped from the domain.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CompensacionXML {

  @XmlElement(name = "codigo")
  private String codigo;

  @XmlElement(name = "tarifa")
  private String tarifa;

  @XmlElement(name = "valor")
  private String valor;

  /** Required no-arg constructor for JAXB deserialization. */
  public CompensacionXML() {}

  /**
   * Creates the XML model for a compensation entry.
   *
   * @param compensation domain compensation to transform
   * @return JAXB-ready XML compensation model
   */
  public static CompensacionXML fromDomain(Compensation compensation) {
    CompensacionXML xml = new CompensacionXML();

    xml.codigo = "1";
    xml.tarifa = compensation.rate().toPlainString();
    xml.valor = compensation.value().toPlainString();

    return xml;
  }
}
