// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.models;

import io.github.opensri.domain.entities.invoice.Retention;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * JAXB model for a retention entry included in the invoice.
 *
 * <p>Applies to invoices using schema version 1.1.0 or 2.1.0, where the SRI allows retentions to be
 * embedded directly in the sales receipt.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RetencionXML {

  @XmlElement(name = "codigo")
  private String codigo;

  @XmlElement(name = "codigoPorcentaje")
  private String codigoPorcentaje;

  @XmlElement(name = "tarifa")
  private String tarifa;

  @XmlElement(name = "valor")
  private String valor;

  /** Required no-arg constructor for JAXB deserialization. */
  public RetencionXML() {}

  /**
   * Creates the XML model for a retention entry.
   *
   * @param retention domain retention to transform
   * @return JAXB-ready XML retention model
   */
  public static RetencionXML fromDomain(Retention retention) {
    RetencionXML xml = new RetencionXML();

    xml.codigo = retention.codigo();
    xml.codigoPorcentaje = retention.codigoPorcentaje();
    xml.tarifa = retention.tarifa().toPlainString();
    xml.valor = retention.valor().toPlainString();

    return xml;
  }
}
